# 🚀 JPlus

> A Java superset language running on the JVM,  
> **JPlus** is a modern programming language that significantly boosts developer productivity while fully leveraging the existing Java ecosystem.

---

## ✨ Project Overview

JPlus is fully compatible with Java, offering modern language features like null safety, type inference, and functional programming to reduce developer burden and maximize productivity.

Notably, **there is currently no ‘superset’ language that keeps Java syntax almost intact while extending the language with features like null checks at the language level.** JPlus aims to fill this gap, providing a language that existing Java developers can naturally learn and adopt.

### Why JPlus?

- **Maintains Java Compatibility**  
  Fully utilizes existing Java libraries and frameworks

- **Enhances Development Productivity**  
  Automates null checks, supports type inference, lambdas, and other modern features

- **Allows Gradual Adoption**  
  Existing Java code can remain unchanged while selectively adopting JPlus syntax

- **Compiles to Java Bytecode**  
  Ensures stable execution and high performance on the JVM


---

## 🛠️ Key Features

| Feature                      | Description                                            |
|------------------------------|--------------------------------------------------------|
| **Strict Null Checking**      | Prevents null reference errors at compile time         |
| **Type Inference**            | Automatically infers types without explicit declarations |
| **Data Classes / Immutable Objects** | Minimizes boilerplate code                             |
| **Lambda and Higher-Order Functions** | Enables functional programming style                   |
| **Pattern Matching**          | Simplifies conditional and type checks                 |
| **Asynchronous Syntax**       | Simple async/await-style asynchronous handling         |
| **Concise Syntax**            | smart casts and more for clearer and shorter code      |

---

## 🚀 IntelliJ Plugin Launch – Coming Soon!

We’re thrilled to announce that the **JPlus IntelliJ Plugin** is about to be released! 🎉

This plugin will allow you to:

- Write JPlus code directly in IntelliJ IDEA
- Enjoy full IDE support including syntax highlighting, code completion, and error checking
- Seamlessly integrate with your existing Java projects

🎥 **Watch the demo video:** [JPlus IntelliJ Plugin Demo](https://youtu.be/0z_aIyBpJso)

Stay tuned – the plugin will be publicly available very soon! Your feedback and early testing will be invaluable to help us polish it before the official launch.

---

## 🚀 New Feature: Apply Syntax for Boilerplate Elimination

JPlus introduces a powerful new **`apply` syntax** to replace common boilerplate patterns previously handled by Lombok annotations. This declarative syntax helps you reduce repetitive code like getters, setters, constructors, equals/hashCode, and builders — all in a clean, compact form.

---

### 🔍 Overview

| Lombok Annotation           | Equivalent `apply` Syntax                    |
|----------------------------|----------------------------------------------|
| `@Data`                    | `data`                                       |
| `@Getter`                  | `getter`                                     |
| `@Setter`                  | `setter`                                     |
| `@EqualsAndHashCode`       | `equals`, `hashCode` or combined `equality` |
| `@ToString`                | `toString`                                   |
| `@NoArgsConstructor`       | `constructors(no)`                           |
| `@AllArgsConstructor`      | `constructors(all)`                          |
| `@RequiredArgsConstructor` | `constructors(required)`                     |
| `@Builder`                 | `builder`                                    |
| `@Value`                   | `value` or `immutable`                        |
| `@Accessors(fluent=true)`  | `accessors(fluent)`                          |
| `@Slf4j` / `@Log`          | `logger(slf4j)` / `logger(java)`             |

> Additional method-level and delegation features are also supported.

---

### 📝 Example Usage

```java
apply data, builder, constructors(all, no);

apply {
    User.Profile: getter, setter, equality, constructor(all);
}

public class User {
    private String name;
    private int age;

    public class Profile {
        String nickname;
    }
}
```

For detailed example content, please refer to the following link.  
[Go to Example 6](#example6)

---


## 🚧 Project Status: MVP

Please note that **JPlus is currently in its MVP (Minimum Viable Product) stage**.  
This means the language is still under active development, features may be incomplete or subject to change, and bugs may exist.

We welcome feedback, bug reports, and contributions from the community to help shape and improve JPlus into a stable and powerful language.

[💸 Support me on Github](https://github.com/sponsors/nieuwmijnleven)

[💸 Support me on PayPal](https://paypal.me/nieuwmijnleven/20)

---

## ⚙️ Installation & Execution

Follow these steps to get started with JPlus and run example programs.

---

### 1. Prerequisites

- **Java Development Kit (JDK) 17 or higher**
  ```bash
  java -version
  ```

- **Gradle** installed (optional if using the provided Gradle wrapper)
  ```bash
  gradle -v
  ``` 
---

### 2. Download JPlus Compiler
Currently, the JPlus compiler is distributed as a Gradle project.
Clone the repository or download the latest release:
```bash
git clone https://github.com/nieuwmijnleven/JPlus.git
cd JPlus
```

---

### 3. Build the Project

Use the Gradle wrapper to build the compiler and run examples:
```bash
./gradlew build
```

---

### 4. Running Examples

To run a specific example file (e.g., NullableType1.jplus), use:
```bash
./gradlew run -Pargs="./src/test/samples/NullableType1.jplus"
```

Replace the argument path with any example you want to execute.

---
### ✅ Example ① - Compile-time null assignment check

JPlus explicitly defines nullability at the type system level and performs **compile-time checks**  
to eliminate the possibility of `NullPointerException` at runtime.

---

#### 📌 Basic Rules

- `Type` → **non-nullable** (cannot be assigned `null`)
- `Type?` → **nullable** (can be assigned `null`)
- `?.` → safely access methods or fields on nullable objects

---

#### 📄 Example – `NullableType1.jplus`

```java
package jplus.example;

public class Main {
    public static void main(String[] args) {
        String? s1 = null;
        String s2 = null;
    }
}
```

- `s1` is a **nullable** variable and can be assigned `null`.
- `s2` is a **non-nullable** variable, so assigning `null` causes a **compile-time error**.

---

#### ▶️ Run Command

```bash
./gradlew run -Pargs="./src/test/samples/NullableType1.jplus"
```

---

#### ❌ Output

```
Error: (line:6, column:8) s2 is a non-nullable variable. But null value is assigned to it.
```

---

By enforcing **explicit type declarations** and **compile-time null checks**,  
JPlus helps prevent null-related bugs early in the development process.

This approach is similar to **strict null checking** found in languages like Kotlin and TypeScript,  
but with the key benefit of being **gradually adoptable while preserving Java syntax**.

---
### ✅ Example ② — Enforcing the null-safe operator (`?.`)

In JPlus, **you must use the `?.` operator when accessing nullable variables.**  
If you try to access fields or methods on a potentially-null object without the null-safe operator, a compile-time error will occur.

---

#### 📄 Example – `NullableType2.jplus`

```java
public class Main {
    public static void main(String[] args) {
        String? s1 = null;
        String s2 = "jplus";

        s1.length();
        s2.length();
    }
}
```

- `s1` is **nullable** but tries to call a method **without using the null-safe operator (`?.`)**  
  → **Compile-time error**

- `s2` is **non-nullable**, so method access is allowed without issue

---

#### ▶️ Run Command

```bash
./gradlew run -Pargs="./src/test/samples/NullableType2.jplus"
```

---

#### ❌ Output

```
Error: (line:8, column:8) s1 is a nullable variable. But it direct accesses to length(). You must consider to use null-safe operator(?.)
```

---

#### ✅ Corrected Code (Works as Expected)

```java
s1?.length(); // null-safe access
s2.length();  // regular access
```

---

As shown above, JPlus **enforces safe access to nullable variables at the language level**,  
helping to eliminate null-related runtime errors before they happen.

---
### ✅ Example ③ — Elvis Operator (`?:`)

JPlus introduces the **Elvis operator (`?:`)**,  
which allows you to easily provide a default or fallback value when a nullable variable is `null`.

---

#### 📄 Example – `ElvisOperator.jplus`

```java
package jplus.example;

public class Main {
    public static void main(String[] args) {
        String? s1 = null;
        String s2 = s1 ?: "jplus";
        System.out.printf("s1 = %s\n", s1 ?: "null-value");
        System.out.printf("s2 = %s\n", s2);
    }
}
```

- `s1` is a nullable variable.
- `s1 ?: "jplus"` assigns `"jplus"` if `s1` is `null`.
- When printing, `s1 ?: "null-value"` safely handles null values.

---

#### ▶️ Run Command

```bash
./gradlew run -Pargs="./src/test/samples/ElvisOperator.jplus"
```

---

#### ✅ Output (Java code generated by JPlus)

```java
package jplus.example;

public class Main {
    public static void main(String[] args) {
        String s1 = null;
        String s2 = (((s1) != null) ? (s1) : "jplus");
        System.out.printf("s1 = %s\n", (((s1) != null) ? (s1) : "null-value"));
        System.out.printf("s2 = %s\n", s2);
    }
}
```

> The Elvis operator expression `x ?: y` is translated to Java's ternary expression `(x != null) ? x : y`.

---
### ✅ Example ④ — Correct Usage of the Null-safe Operator (`?.`)

In JPlus, the use of the **null-safe access operator (`?.`) is enforced** for nullable variables.  
This prevents null reference errors and allows safe method calls on potentially null values.

---

#### 📄 Example – `NullsafeOperator.jplus`

```java
public class Main {
    public static void main(String[] args) {
        String? s1 = null;
        String s2 = "jplus";
        System.out.printf("the length of s1 : %d\n", s1?.length());
        System.out.printf("the length of s2 : %d\n", s2.length());
    }
}
```

- `s1` is a nullable variable.
- `s1?.length()` safely returns `null` if `s1` is `null`.
- `s2` is non-nullable, so calling `length()` is valid without safety checks.

---

#### ▶️ Run Command

```bash
./gradlew run -Pargs="./src/test/samples/NullsafeOperator.jplus"
```

---

#### ✅ Output (Java code generated by JPlus)

```java
package jplus.example;

public class Main {
    public static void main(String[] args) {
        String s1 = null;
        String s2 = "jplus";
        System.out.printf("the length of s1 : %d\n", (s1 != null) ? s1.length() : null);
        System.out.printf("the length of s2 : %d\n", s2.length());
    }
}
```

---

This example demonstrates how JPlus handles the null-safe operator for nullable variables  
and clearly shows how to write code that adheres to JPlus's null safety rules.

---
### ✅ Example ⑤ — Combining `?.` and `?:` Operators

JPlus supports **combining the null-safe access operator (`?.`) and the Elvis operator (`?:`)**  
to simplify complex null-handling logic into clean and concise expressions.

---

#### 📄 Example – `NullsafeWithElvisOperator.jplus`

```java
package jplus.example;

public class Main {
    public static void main(String[] args) {
        String? s1 = null;
        String s2 = s1 ?: "jplus";
        System.out.printf("the length of s1 : %d\n", s1?.length() ?: 0);
        System.out.printf("the length of s2 : %d\n", s2.length());
    }
}
```

- `s1` is a nullable variable.
- `s1 ?: "jplus"` → assigns `"jplus"` if `s1` is null.
- `s1?.length() ?: 0` → safely calls `length()` on `s1`, returns `0` if `s1` is null.
- By combining both operators, **null handling becomes safe and concise**.

---

#### ▶️ Run Command

```bash
./gradlew run -Pargs="./src/test/samples/NullsafeWithElvisOperator.jplus"
```

---

#### ✅ Output (Java code generated by JPlus)

```java
package jplus.example;

public class Main {
    public static void main(String[] args) {
        String s1 = null;
        String s2 = (((s1) != null) ? (s1) : "jplus");
        System.out.printf(
                "the length of s1 : %d\n",
                (((((s1 != null) ? s1.length() : null)) != null)
                        ? (((s1 != null) ? s1.length() : null))
                        : 0));
        System.out.printf("the length of s2 : %d\n", s2.length());
    }
}
```

> The expression `s1?.length() ?: 0` is translated into a nested conditional check in Java:  
> `((s1 != null) ? s1.length() : null) != null ? ... : 0`, ensuring safe execution.

---
<a name="example6"></a>
### ✅ Example ⑥ — Using apply for Data Class and Nested Class Boilerplate Elimination

JPlus introduces the apply keyword to replace common Java boilerplate code such as getters, setters, constructors, builders, and more.
It serves as a language-level alternative to Lombok annotations, offering a clean and declarative syntax.

---

#### 📄 Example – `ApplyStatement.jplus`

```java
package com.example;

apply data, constructor(required, all, no), builder;
apply {
    User.Profile: getter, setter, equality, constructor(all);
}

public class User {
    private String name;
    private int age;

    public class Profile {
        String nickname;
    }
}
```
- `data`:	Automatically generates getters, setters, equals(), hashCode(), and toString()
- `builder`: Generates a User.Builder class for fluent object creation
- `constructors(required, all, no)`:Generates a constructor with all/required fields and a no-argument constructor
- `equality`: Generates equals() and hashCode() methods
- `apply { User.Profile: ... }`: Applies boilerplate generation specifically to the Profile inner class
---

#### ▶️ Run Command

```bash
./gradlew run -Pargs="./src/test/samples/ApplyStatement.jplus"
```
---

#### ✅ Output (Java code generated by JPlus)

```java
package com.example;

//apply data, constructor(required, all, no), builder;
//apply {
//    User.Profile: getter, setter, equality, constructor(all);
//}

public class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User() {}

    public class Profile {
        String nickname;

        public Profile(String nickname) {
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Profile profile = (Profile) o;
            return java.util.Objects.equals(nickname, profile.nickname);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(nickname);
        }
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name+ ", " + "age=" + age + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return age == user.age
                && java.util.Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, age);
    }

    public static class Builder {
        private String name;
        private int age;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(name, age);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
```
---
#### 🎯 Benefits of This Approach

- ✅ Eliminates the need for manual method and constructor generation
- ✅ Clean, declarative syntax – no annotations or external dependencies
- ✅ Fine-grained control over nested types (e.g. inner classes)
- ✅ All features translate into standard Java code at compile time
---
#### 📘 Tip: Use apply {} block for Nested Classes

The apply block allows you to target specific nested or inner classes with scoped boilerplate logic:
```java
apply {
    Outer.Inner: getter, setter;
}
```

This allows developers to keep code DRY and expressive even with deeply nested structures.

---

### 📌 Summary

This example demonstrates a **natural combination** of the following features:

- ✅ **Null-safe method call** (`s1?.length()`)  
  Ensures safe access to nullable variables without throwing `NullPointerException`.

- ✅ **Default fallback value** (`?: 0`)  
  Provides a default value when an expression evaluates to `null`, improving code safety and readability.

- ✅ **Declarative boilerplate generation with `apply`**  
  The `apply` statement allows you to automatically generate common methods like `getters`, `setters`, `equals()`, `hashCode()`, `toString()`, and constructors without relying on annotations or third-party libraries like Lombok.

JPlus allows even complex null-handling logic to be expressed **safely and concisely** with its extended syntax.

---

### 📚 Summary of Examples

| Example File                         | Description                                                |
|-------------------------------------|------------------------------------------------------------|
| `NullableType1.jplus`               | Error when assigning `null` to a non-nullable variable     |
| `NullableType2.jplus`               | Error when accessing a nullable variable without `?.`      |
| `ElvisOperator.jplus`               | Handling null defaults using the Elvis operator `?:`       |
| `NullsafeOperator.jplus`            | Safe method call using the null-safe operator `?.`         |
| `NullsafeWithElvisOperator.jplus`  | Combining `?.` and `?:` for safe and concise null handling |
| `ApplyStatement.jplus`              | Using apply to generate boilerplate and builder logic cleanly|

---

## 🌟 Reference Examples

- **Kotlin:** The leading JVM Java superset language

- **Scala:** Combines functional and object-oriented paradigms, powerful but complex

- **Groovy:** Emphasizes dynamic typing and conciseness

- **Xtend:** Provides Java compatibility with concise syntax


---

## 🎯 JPlus Differentiators

- Maintains maximum similarity to Java’s standard syntax

- **No existing ‘superset’ language keeps Java syntax almost unchanged while extending language-level null checks; JPlus fills this role**

- Enforces strict null safety

- Supports gradual and flexible syntax extensions

- Offers a gentle learning curve for Java developers


---

## 🤝 Sponsorship & Support

JPlus is currently in its MVP stage, and your support can make a big difference in shaping its future.

- **Report bugs, suggest features, and contribute code** — every contribution helps!
- By sponsoring, you help accelerate development and gain early access to new features.
- We are preparing exclusive perks for sponsors as a token of our gratitude.

If you want to support the project financially, please visit our sponsorship page:

[Become a Sponsor via Github](https://github.com/sponsors/nieuwmijnleven)

[Become a Sponsor via PayPal.me](https://paypal.me/nieuwmijnleven)

Thank you for helping us build a better, safer, and more productive programming language!

---

## 📄 License

JPlus is licensed under the [Apache License 2.0](./LICENSE).

---