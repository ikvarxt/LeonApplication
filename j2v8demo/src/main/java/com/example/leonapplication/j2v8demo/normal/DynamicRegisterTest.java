package com.example.leonapplication.j2v8demo.normal;

import com.eclipsesource.v8.V8Object;

import java.lang.reflect.Method;

import androidx.annotation.NonNull;

public class DynamicRegisterTest implements ScriptTester {

  @NonNull
  @Override
  public String getScript() {
    return "function main(bool) {\n" +
      "   console.log(DynamicRegisterTest.getString());\n" +
      "}\n";
  }

  @NonNull
  @Override
  public String[] getMainArgs() {
    return new String[]{"false"};
  }

  @Override
  public void registerMethods(@NonNull V8Object methodHolder) {
    DynamicRegisterClass test = new DynamicRegisterClass();
    Method[] methods = test.getClass().getMethods();
    for (Method m : methods) {
      methodHolder.registerJavaMethod(test, m.getName(), m.getName(), m.getParameterTypes());
    }
  }

  @Override
  public void attach(@NonNull V8Manager manager, @NonNull String name) {
    ScriptTester.DefaultImpls.attach(this, manager, name);
  }

  /**
   * @noinspection unused
   */
  static class DynamicRegisterClass {

    public static int getValue() {
      return 0;
    }

    public static String getString() {
      return "test success dynamic";
    }

    public static void setValue(int value) {
    }

    public static void setString(String v) {
    }

    public int getMyValue() {
      return 0;
    }

    public void setMyValue(int v) {
    }
  }
}
