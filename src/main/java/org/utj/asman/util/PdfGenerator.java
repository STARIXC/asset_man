package org.utj.asman.util;

import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;

/**
 * Simple PDF generator that converts an HTML string to a PDF byte array.
 * Uses Flying Saucer (ITextRenderer) which is already on the classpath via the
 * {@code flying-saucer-pdf} dependency.
 */
public class PdfGenerator {

    /**
     * Generates a PDF document from the supplied HTML string.
     *
     * @param html the fully‑rendered HTML (including CSS) to be converted
     * @return a byte array containing the PDF data
     * @throws Exception if rendering fails
     */
    public static byte[] generatePdfFromHtml(String html) throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            // Flying Saucer expects a base URL for resolving relative resources (CSS,
            // images)
            // Using "" works for classpath resources that are embedded directly in the
            // HTML.
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os);
            return os.toByteArray();
        }
    }
}
