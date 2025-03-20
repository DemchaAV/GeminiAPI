package anki.creator;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
@Getter
@Slf4j
public class Model {
    public static final int FRONT_BACK = 0;
    public static final int CLOZE = 1;
    public static final String DEFAULT_LATEX_PRE =
            "\\documentclass[12pt]{article}\n\\special{papersize=3in,5in}\n"
            + "\\usepackage[utf8]{inputenc}\n\\usepackage{amssymb,amsmath}\n"
            + "\\pagestyle{empty}\n\\setlength{\\parindent}{0in}\n\\begin{document}\n";
    public static final String DEFAULT_LATEX_POST = "\\end{document}";

    private int modelId;
    private String name;
    private List<Map<String, Object>> fields;
    private List<Map<String, Object>> templates;
    private String css;
    private int modelType;
    private String latexPre;
    private String latexPost;
    private int sortFieldIndex;

    public Model(int modelId, String name, Object fields, Object templates, String css, int modelType,
                 String latexPre, String latexPost, int sortFieldIndex) {
        log.info("Initializing GeminiModel with ID: {} and name: {}", modelId, name);
        this.modelId = modelId;
        this.name = name;
        this.css = css;
        this.modelType = modelType;
        this.latexPre = latexPre;
        this.latexPost = latexPost;
        this.sortFieldIndex = sortFieldIndex;
        setFields(fields);
        setTemplates(templates);
    }

    public void setFields(Object fields) {
        if (fields instanceof List) {
            this.fields = (List<Map<String, Object>>) fields;
        } else if (fields instanceof String) {
            Yaml yaml = new Yaml();
            this.fields = yaml.load((String) fields);
        } else {
            this.fields = new ArrayList<>();
        }
        log.debug("Fields set: {}", this.fields);
    }

    public void setTemplates(Object templates) {
        if (templates instanceof List) {
            this.templates = (List<Map<String, Object>>) templates;
        } else if (templates instanceof String) {
            Yaml yaml = new Yaml();
            this.templates = yaml.load((String) templates);
        } else {
            this.templates = new ArrayList<>();
        }
        log.debug("Templates set: {}", this.templates);
    }

    public List<Object[]> computeRequiredFields() {
        log.info("Computing required fields for GeminiModel: {}", name);
        try {
            List<Object[]> req = new ArrayList<>();
            String sentinel = "SeNtInEl";
            List<String> fieldNames = new ArrayList<>();
            for (Map<String, Object> field : fields) {
                fieldNames.add((String) field.get("name"));
            }

            for (int templateOrd = 0; templateOrd < templates.size(); templateOrd++) {
                Map<String, Object> template = templates.get(templateOrd);
                List<Integer> requiredFields = new ArrayList<>();

                for (int fieldOrd = 0; fieldOrd < fieldNames.size(); fieldOrd++) {
                    String fieldName = fieldNames.get(fieldOrd);
                    Map<String, String> fieldValues = new HashMap<>();
                    for (String name : fieldNames) {
                        fieldValues.put(name, sentinel);
                    }
                    fieldValues.put(fieldName, "");

                    String rendered = renderMustache((String) template.get("qfmt"), fieldValues);

                    if (!rendered.contains(sentinel)) {
                        requiredFields.add(fieldOrd);
                    }
                }

                if (!requiredFields.isEmpty()) {
                    req.add(new Object[]{templateOrd, "all", requiredFields});
                    continue;
                }

                requiredFields.clear();
                for (int fieldOrd = 0; fieldOrd < fieldNames.size(); fieldOrd++) {
                    String fieldName = fieldNames.get(fieldOrd);
                    Map<String, String> fieldValues = new HashMap<>();
                    for (String name : fieldNames) {
                        fieldValues.put(name, "");
                    }
                    fieldValues.put(fieldName, sentinel);

                    String rendered = renderMustache((String) template.get("qfmt"), fieldValues);

                    if (rendered.contains(sentinel)) {
                        requiredFields.add(fieldOrd);
                    }
                }

                if (requiredFields.isEmpty()) {
                    log.error("Could not compute required fields for template: {}",template);
                    throw new RuntimeException("Could not compute required fields for template: " + template);
                }

                req.add(new Object[]{templateOrd, "any", requiredFields});
            }
            return req;
        } catch (Exception e) {
            log.error("Error computing required fields: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Map<String, Object> toJson(long timestamp, long deckId) {
        templates = new ArrayList<>(templates);

        for (int i = 0; i < templates.size(); i++) {
            Map<String, Object> tmpl = new HashMap<>(templates.get(i)); // Создаём изменяемую копию
            tmpl.putIfAbsent("bafmt", "");
            tmpl.putIfAbsent("bqfmt", "");
            tmpl.putIfAbsent("bfont", "");
            tmpl.putIfAbsent("bsize", 0);
            tmpl.putIfAbsent("did", null);
            tmpl.put("ord", i);
            templates.set(i, tmpl); // Сохраняем обновлённый Map в список
        }

        fields = new ArrayList<>(fields);

        for (int i = 0; i < fields.size(); i++) {
            Map<String, Object> field = new HashMap<>(fields.get(i)); // Создаём изменяемую копию
            field.putIfAbsent("font", "Liberation Sans");
            field.putIfAbsent("media", new ArrayList<>());
            field.putIfAbsent("rtl", false);
            field.putIfAbsent("size", 20);
            field.putIfAbsent("sticky", false);
            field.put("ord", i);
            fields.set(i, field); // Сохраняем обновлённый Map в список
        }

        // Создаём JSON объект
        Map<String, Object> json = new HashMap<>();
        json.put("css", css);
        json.put("did", deckId);
        json.put("flds", fields);
        json.put("id", String.valueOf(modelId));
        json.put("latexPost", latexPost);
        json.put("latexPre", latexPre);
        json.put("latexsvg", false);
        json.put("mod", timestamp);
        json.put("name", name);
        json.put("req", computeRequiredFields());
        json.put("sortf", sortFieldIndex);
        json.put("tags", new ArrayList<>());
        json.put("tmpls", templates);
        json.put("type", modelType);
        json.put("usn", -1);
        json.put("vers", new ArrayList<>());

        return json;
    }


    private String renderMustache(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }

    @Override
    public String toString() {
        return "GeminiModel{" +
               "modelId=" + modelId +
               ", name='" + name + '\'' +
               ", fields=" + fields +
               ", templates=" + templates +
               ", css='" + css + '\'' +
               ", modelType=" + modelType +
               '}';
    }


}
