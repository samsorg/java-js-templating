package com.gmail.samkzero.js;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

@SuppressWarnings("restriction")
public class Handlebars {

    private static final String SCRIPT_E = "nashorn";
    private static final String HBARS_JS = "handlebars-v3.0.3.js";
    private static final String JS_HANDLEBARS = "Handlebars";
    private static final String JS_JSON = "JSON";

    private static final String JS_M_PARSE = "parse";
    private static final String JS_M_COMPILE = "compile";

    private ScriptEngine scriptEngine;
    private final Object handlebars;
    private final Object JSON;

    public Handlebars(ScriptEngine scriptEngine) throws FileNotFoundException, ScriptException {
        this.scriptEngine = scriptEngine;
        FileReader fileReader = new FileReader(getLibFile());
        scriptEngine.eval(fileReader);
        handlebars = scriptEngine.eval(JS_HANDLEBARS);
        JSON = scriptEngine.eval(JS_JSON);
    }

    public String render(String source, String context) throws NoSuchMethodException, ScriptException, ClassNotFoundException {
        Invocable invocable = (Invocable) scriptEngine;
        Object json = invocable.invokeMethod(JSON, JS_M_PARSE, context);
        ScriptObjectMirror template = (ScriptObjectMirror) invocable.invokeMethod(handlebars, JS_M_COMPILE, source);
        return (String) template.call(null, json);
    }

    private static File getLibFile() {
        ClassLoader classLoader = Handlebars.class.getClassLoader();
        return new File(classLoader.getResource(HBARS_JS).getFile());
    }

    public static void main(String[] args) throws FileNotFoundException, ScriptException, NoSuchMethodException, ClassNotFoundException {

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(SCRIPT_E);

        String template = "<div class=\"entry\"><h1>{{title}}</h1><div class=\"body\">{{body}}</div></div>";
        String context = "{\"title\": \"My New Post\", \"body\": \"This is my first post!\"}";

        Handlebars handlebars = new Handlebars(scriptEngine);
        String rendered = handlebars.render(template, context);
        System.out.println(rendered);

    }

}
