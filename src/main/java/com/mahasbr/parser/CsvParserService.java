package com.mahasbr.parser;

import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CsvParserService implements FileParserService {

	private static final Charset WINDOWS_1252 = Charset.forName("windows-1252");
	private static final int CHARSET_PROBE_BUFFER_SIZE = 8192;
	private static final int UTF16_PROBE_BYTES = 512;

	@Override
	public boolean supports(String fileName) {
		return fileName != null && fileName.toLowerCase().endsWith(".csv");
	}

	@Override
	public void parse(Path filePath, CsvUploadRowHandler rowHandler) throws IOException {
		Charset charset = resolveCharset(filePath);
		try {
			parseWithCharset(filePath, rowHandler, charset);
		} catch (MalformedInputException exception) {
			if (WINDOWS_1252.equals(charset)) {
				throw new IllegalArgumentException(
						"Unable to read the CSV file. Please save the file as UTF-8, ANSI, or Excel CSV and try again.",
						exception);
			}
			log.warn("CSV charset decode failed with {} for file {}. Retrying with windows-1252.", charset, filePath,
					exception);
			try {
				parseWithCharset(filePath, rowHandler, WINDOWS_1252);
			} catch (CsvValidationException csvValidationException) {
				throw new IllegalArgumentException("Invalid CSV file format.", csvValidationException);
			}
		} catch (CsvValidationException exception) {
			throw new IllegalArgumentException("Invalid CSV file format.", exception);
		}
	}

	private void parseWithCharset(Path filePath, CsvUploadRowHandler rowHandler, Charset charset)
			throws IOException, CsvValidationException {
		try (Reader fileReader = newBomAwareReader(filePath, charset);
				CSVReader reader = new CSVReaderBuilder(fileReader)
						.withCSVParser(new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build()).build()) {
			String[] headers = reader.readNext();
			if (headers == null || headers.length == 0) {
				return;
			}

			String[] normalizedHeaders = Arrays.stream(headers).map(this::normalizeHeader).toArray(String[]::new);
			int rowNumber = 1;
			String[] values;

			while ((values = reader.readNext()) != null) {
				rowNumber++;
				Map<String, String> rowData = new LinkedHashMap<>();
				boolean hasValue = false;

				for (int index = 0; index < normalizedHeaders.length; index++) {
					String value = index < values.length && values[index] != null ? values[index].trim() : "";
					if (!value.isBlank()) {
						hasValue = true;
					}
					rowData.put(normalizedHeaders[index], value);
				}

				if (hasValue) {
					rowHandler.handle(rowNumber, rowData);
				}
			}
		}
	}

	private Charset resolveCharset(Path filePath) throws IOException {
		ByteOrderMark bom = detectBom(filePath);
		if (ByteOrderMark.UTF_8.equals(bom)) {
			return StandardCharsets.UTF_8;
		}
		if (ByteOrderMark.UTF_16LE.equals(bom)) {
			return StandardCharsets.UTF_16LE;
		}
		if (ByteOrderMark.UTF_16BE.equals(bom)) {
			return StandardCharsets.UTF_16BE;
		}

		if (looksLikeUtf16(filePath)) {
			return inferUtf16Variant(filePath);
		}

		if (isDecodable(filePath, StandardCharsets.UTF_8)) {
			return StandardCharsets.UTF_8;
		}

		return WINDOWS_1252;
	}

	private Reader newBomAwareReader(Path filePath, Charset charset) throws IOException {
		BOMInputStream inputStream = BOMInputStream.builder().setPath(filePath)
				.setByteOrderMarks(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE).get();
		return new java.io.BufferedReader(new java.io.InputStreamReader(inputStream, charset));
	}

	private ByteOrderMark detectBom(Path filePath) throws IOException {
		try (BOMInputStream inputStream = BOMInputStream.builder().setPath(filePath)
				.setByteOrderMarks(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE).get()) {
			return inputStream.getBOM();
		}
	}

	private boolean isDecodable(Path filePath, Charset charset) throws IOException {
		CharsetDecoder decoder = charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT);
		try (var channel = Files.newByteChannel(filePath)) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(CHARSET_PROBE_BUFFER_SIZE);
			CharBuffer charBuffer = CharBuffer.allocate(CHARSET_PROBE_BUFFER_SIZE);

			while (channel.read(byteBuffer) != -1) {
				byteBuffer.flip();
				decoder.decode(byteBuffer, charBuffer, false);
				byteBuffer.compact();
				charBuffer.clear();
			}

			byteBuffer.flip();
			decoder.decode(byteBuffer, charBuffer, true);
			decoder.flush(charBuffer);
			return true;
		} catch (CharacterCodingException exception) {
			return false;
		}
	}

	private boolean looksLikeUtf16(Path filePath) throws IOException {
		byte[] sample = readProbeBytes(filePath);
		if (sample.length < 4) {
			return false;
		}

		int evenNulls = 0;
		int oddNulls = 0;
		for (int index = 0; index < sample.length; index++) {
			if (sample[index] == 0) {
				if (index % 2 == 0) {
					evenNulls++;
				} else {
					oddNulls++;
				}
			}
		}

		int threshold = Math.max(4, sample.length / 10);
		return evenNulls >= threshold || oddNulls >= threshold;
	}

	private Charset inferUtf16Variant(Path filePath) throws IOException {
		byte[] sample = readProbeBytes(filePath);
		int evenNulls = 0;
		int oddNulls = 0;
		for (int index = 0; index < sample.length; index++) {
			if (sample[index] == 0) {
				if (index % 2 == 0) {
					evenNulls++;
				} else {
					oddNulls++;
				}
			}
		}
		return oddNulls >= evenNulls ? StandardCharsets.UTF_16LE : StandardCharsets.UTF_16BE;
	}

	private byte[] readProbeBytes(Path filePath) throws IOException {
		try (var inputStream = Files.newInputStream(filePath)) {
			return inputStream.readNBytes(UTF16_PROBE_BYTES);
		}
	}

	private String normalizeHeader(String header) {
		if (header == null) {
			return "";
		}

		return header.replace("\uFEFF", "").trim().toUpperCase();
	}
}
