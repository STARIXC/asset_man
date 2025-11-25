package org.utj.asman.service;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.utj.asman.model.AssetRecord;
import org.utj.asman.model.Facility;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    public ByteArrayOutputStream generateAssignmentFormsPdf(Facility facility, List<AssetRecord> assets) throws DocumentException, IOException {
        Context context = new Context();

        // Using Java 8 compatible HashMap
        Map<String, Object> variables = new HashMap<>();
        variables.put("facility", facility);
        variables.put("assets", assets);
        context.setVariables(variables);

        String htmlContent = templateEngine.process("pdf/assignment_form", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream;
    }
}