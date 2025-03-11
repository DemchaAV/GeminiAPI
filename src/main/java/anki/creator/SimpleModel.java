package anki.creator;

import java.util.List;
import java.util.Map;

class SimpleModel extends Model {
    private long id;
    private String name;
    private List<Map<String, String>> fields;
    private List<Map<String, String>> templates;
    private String css;


    public SimpleModel(long modelId, String name, List<Map<String, String>> fields, List<Map<String, String>> templates, String css) {
        super((int)modelId, name, fields, templates, css, FRONT_BACK, DEFAULT_LATEX_PRE, DEFAULT_LATEX_POST, 0);
    }

}

