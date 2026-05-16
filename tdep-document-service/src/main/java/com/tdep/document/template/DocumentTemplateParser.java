package com.tdep.document.template;

import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class DocumentTemplateParser {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^{}]+)}}");

    public List<String> parse(InputStream inputStream) {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            Set<String> placeholders = new LinkedHashSet<>();
            document.getParagraphs().forEach(paragraph -> collect(paragraph.getText(), placeholders));
            document.getTables().forEach(table -> table.getRows().forEach(row ->
                    row.getTableCells().forEach(cell -> collect(cell.getText(), placeholders))));
            return new ArrayList<>(placeholders);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "模板解析失败: " + ex.getMessage());
        }
    }

    private void collect(String text, Set<String> placeholders) {
        if (text == null) {
            return;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            placeholders.add(matcher.group(1).trim());
        }
    }
}
