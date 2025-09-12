package com.mahasbr.response;

import lombok.Data;

@Data
public class ExcelFileUpaloadResult {
	
	private String originalName;
    private String savedName;
    private String status;
    private String reason;

    public ExcelFileUpaloadResult(String originalName, String savedName, String status, String reason) {
        this.originalName = originalName;
        this.savedName = savedName;
        this.status = status;
        this.reason = reason;
    }

    public static ExcelFileUpaloadResult success(String original, String saved) {
        return new ExcelFileUpaloadResult(original, saved, "ok", null);
    }

    public static ExcelFileUpaloadResult failure(String original, String reason) {
        return new ExcelFileUpaloadResult(original, null, "error", reason);
    }

}
