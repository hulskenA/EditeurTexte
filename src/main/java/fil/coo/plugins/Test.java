package plugins;

import fil.coo.plugin.Plugin;


public class Test implements Plugin {
  public String transform(String s) {
    return s;
  }

  public String getLabel() {
    return "Nothing";
  }

  public String helpMessage() {
    return "this plugin do nothing";
  }
}
