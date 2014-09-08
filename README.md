# tinyvalidator

This is a tiny validation library for Java.

## SYNOPSIS

    Validator validator = new Validator();
    List<Violation<Foo>> violations = validator.validate(foo);
    DefaultMessageGenerator generator = new DefaultMessageGenerator();
    String msg = violations.stream()
      .map(it -> generator.generateMessage(it))
      .collect(Collectors.joining(","));

Bean definition:

    @Data
    public static class Foo {
      @Pattern(regexp = "\\A[0-9]+\\z")
      private String bar;
    }

## Validation rules

### `@NotNull`

    @Data
    public static class Foo {
      @NotNull
      private String bar;
    }

bar should not be null.

### `@Size`

    @Data
    public static class Foo {
      @Size(min=2, max=5)
      private String bar;
    }

`bar` should be grater than or equals 2, less than or equals 5.

min's default value is 0, max's default value is `Integer.MAX_VALUE`.

### `@Pattern`

    @Data
    public static class Foo {
      @Pattern(regexp="\\A[0-9]+\z")
      private String bar;
    }

`bar` should match the regular rexpression.

## HOW DO I DEBUG VALIDATION RULE USING THIS LIBRARY?

This library output logs with slf4j. The package name is under `the me.geso.tinyvalidator`. Please set the debug log level as `debug`.

If you are using slf4j-simple, please try following VM options.

    -Dorg.slf4j.simpleLogger.defaultLogLevel=debug


## HOW DO I IMPLEMENT MY OWN RULE?

 1. Implement annotation
 2. Implement rule
 3. Implement message generator
 4. Register these objects!

## RULES

TBD

## MOTIVATION

I want to use tiny and thin validation library.

## AUTHOR

Tokuhiro Matsuno <tokuhirom@gmail.com>

## LICENSE

  The MIT License (MIT)
  Copyright © 2014 Tokuhiro Matsuno, http://64p.org/ <tokuhirom@gmail.com>

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the “Software”), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.