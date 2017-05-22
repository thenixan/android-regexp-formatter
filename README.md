[![](https://jitpack.io/v/thenixan/android-regexp-formatter.svg)](https://jitpack.io/#thenixan/android-regexp-formatter)

To add the library to your project:
- add these lines to your root project's `build.gradle` file
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

- add these lines to the dependencies block in your module's `build.gradle` file
```
dependencies {
        compile 'com.github.thenixan:android-regexp-formatter:v0.0.+'
}
```

Library allows you to validate the input of the Android's default implementation of `EditText` via the `TextWatcher` interface. All you have to do is the create an instance of the `RegExpFormatter` class using it's constructor by passing it the regular expression of the valid input.

For example you want your users to enter some kind of code that look like `123-456`. It means it's valid only if it consists of two blocks of three digits divided by `-` sign. Regular expression for that input is `\d{3}-\d{3}`.

Declare your formatter as the `Acitivty`'s variable:
```
val regExpFormatter = RegExpFormatter("\\d{3}-\\d{3}")
```

After the `setContentView` you should set your `regExpFormatter` as the `TextWatcher` for the target `EditText`:
```
(findViewById(R.id.test_edit) as (EditText)).addTextChangedListener(regExpFormatter)
```

Optionally you can provide `inputType` for the `EditText` to set the proper keyboard type:
```
(findViewById(R.id.test_edit) as (EditText)).inputType = regExpFormatter.inputType
```

Also you can check if the input is correct or not:
```
if (regExpFormatter.check((findViewById(R.id.test_edit) as (EditText)).text.toString())) {
    // Do something
}
```

----

Possible formatting variants:

- `\d+` – at least one digit
- `\w*` - unlimited length of characters
- `[A-Z0-9]{2,4}` - two, three or four capital letters or digits

----

Examples:

Mask: `asd\d{3}asd`
Input: `000`
Result: `asd000asd`

Mask: `asd\w{3}`
Input: `www`
Result: `asdwww`

Mask: `asdw\w{3}`
Input: `www`
Result: `asdwww`

Mask: `asdw\w{3}`
Input: `asd`
Result: `asd`

Mask: `\d{2}12\d{2,4}`
Input: `9999`
Result: `991299`

Mask: `\d{1,2}test`
Input: `1t2`
Result: `1test`
