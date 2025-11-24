package com.mahasbr.util;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class RowValidator {

  private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
  private static final Pattern PIN6 = Pattern.compile("^\\d{6}$");
  private static final Pattern INTEGER = Pattern.compile("^-?\\d+$");

  public Optional<String> validate(Map<String, String> row) {
    String email = row.getOrDefault("emailAddress", "").trim();
    if (!email.isBlank() && !EMAIL.matcher(email).matches()) {
      return Optional.of("Invalid emailAddress: " + email);
    }

    String pin = row.getOrDefault("pinCode", "").trim();
    if (!pin.isBlank() && !PIN6.matcher(pin).matches()) {
      return Optional.of("Invalid pinCode (expect 6 digits): " + pin);
    }

    String headPin = row.getOrDefault("headOfficePinCode", "").trim();
    if (!headPin.isBlank() && !PIN6.matcher(headPin).matches()) {
      return Optional.of("Invalid headOfficePinCode (expect 6 digits): " + headPin);
    }

    String total = row.getOrDefault("totalNumberOfPersonsWorking", "").trim();
    if (!total.isBlank() && !INTEGER.matcher(total).matches()) {
      return Optional.of("Invalid totalNumberOfPersonsWorking: " + total);
    }

    String gst = row.getOrDefault("gstNumber", "").trim();
    if (!gst.isBlank() && gst.length() > 15) {
      return Optional.of("Invalid gstNumber (max 15 chars): " + gst);
    }

    return Optional.empty();
  }
}
