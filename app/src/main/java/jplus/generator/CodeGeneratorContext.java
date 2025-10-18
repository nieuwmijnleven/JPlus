package jplus.generator;

import jplus.util.FragmentedText;

public class CodeGeneratorContext {

    private static CodeGeneratorContext instance = new CodeGeneratorContext();

    private String original;

    private FragmentedText fragmentedText;

    private CodeGeneratorContext() {}

    public static CodeGeneratorContext getInstance() {
        return instance;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public FragmentedText getFragmentedText() {
        return this.fragmentedText;
    }

    public void setFragmentedText(FragmentedText fragmentedText) {
        this.fragmentedText = fragmentedText;
    }
}
