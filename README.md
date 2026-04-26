# BRCM Automation — Selenium + TestNG

A beginner-friendly Selenium Java framework for UI test automation.

---

## Quick start

```bash
# Run the full suite
mvn test

# Run only the smoke suite
mvn test -DsuiteXmlFile=smoke.xml

# Override the browser or URL from the command line
mvn test -Dbrowser=firefox -Dbase.url=https://mis-release.bromcom.dev/
```

## Project layout

```
src/main/java/com/brcm/
├── ui/
│   ├── core/DriverFactory.java        Creates Chrome/Firefox/Edge drivers
│   ├── pages/
│   │   ├── BasePage.java              Parent class — provides driver, actions, waits
│   │   ├── LoginPage.java             Example Page Object
│   │   └── DashboardPage.java         Example Page Object
│   └── utils/
│       ├── ConfigReader.java          Reads *.properties from classpath
│       ├── TestCredentials.java       Login values from credentials.properties
│       ├── UIActions.java             click, type, select, hover, …
│       ├── WaitUtils.java             Explicit waits
│       ├── ExcelUtils.java            Read Excel data-driven rows
│       ├── RetryAnalyzer.java         Retry failed tests N times
│       ├── ExtentManager.java         HTML reports
│       ├── ScreenshotUtils.java       Capture on failure
│       └── TestListener.java          Hooks into TestNG lifecycle

src/main/resources/
├── config.properties                  Framework settings
└── credentials.properties             Login values

src/test/java/com/brcm/
├── base/UIBaseTest.java               Extend this for UI tests
└── tests/ui/LoginTest.java            Example tests

testng.xml                             Full test suite
smoke.xml                              Smoke subset
```

## Writing your first UI test (10 lines)

```java
package com.brcm.tests.ui;

import com.brcm.base.BaseTest;
import pages.com.brcm.LoginPage;
import com.brcm.ui.utils.DefaultTestCredentials;
import org.testng.annotations.Test;

public class MyFirstTest extends UIBaseTest {
    @Test(groups = "ui")
    public void loginWorks() {
        new LoginPage(driver)
                .open(baseUrl)
                .login(DefaultTestCredentials.schoolId(),
                        DefaultTestCredentials.username(),
                        DefaultTestCredentials.password());
    }
}
```

Then add it to `testng.xml`:
```xml
<class name="com.brcm.tests.ui.MyFirstTest"/>
```

## Adding a new Page Object

```java
package com.brcm.ui.pages;

import base.pages.com.brcm.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfilePage extends BasePage {

    @FindBy(id = "firstName")
    private WebElement firstNameInput;
    @FindBy(id = "save")
    private WebElement saveButton;

    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    public ProfilePage enterFirstName(String name) {
        actions.type(firstNameInput, name);
        return this;
    }

    public void save() {
        actions.click(saveButton);
        waits.waitForPageReady();
    }
}
```

## Changing settings

| What you want to change | Where |
|---|---|
| Browser, headless, timeouts | `src/main/resources/config.properties` |
| Username / password        | `src/main/resources/credentials.properties` |
| Which tests run            | `testng.xml` / `smoke.xml` (groups + classes) |
| Override at runtime        | `-Dkey=value` on the `mvn` command line |

Suite-level overrides (applies to all tests in a suite):
```xml
<parameter name="BASE_URL"   value="https://staging.example.com/"/>
<parameter name="BROWSER"    value="firefox"/>
<parameter name="RESOLUTION" value="1366x768"/>
```

## Reports & screenshots

After a run, open:
- `test-output/extent/extent-report-*.html` — HTML report
- `test-output/screenshots/*.png` — screenshots captured on failure

## Common helpers

| Helper | What it does |
|---|---|
| `actions.click(el)` | Waits until the element is clickable, then clicks |
| `actions.type(el, "x")` | Clears and types into a text field |
| `actions.selectByVisibleText(el, "x")` | Selects a `<select>` option |
| `waits.waitForPageReady()` | Waits for `document.readyState == complete` |
| `waits.visibilityOf(el)` | Waits for the element to be visible |

## Troubleshooting

- **`IllegalArgumentException: Unsupported browser`** — set `browser=chrome|firefox|edge` in `config.properties`.
- **`config.properties not found on classpath`** — make sure the file lives under `src/main/resources/`. Maven will put it on the classpath automatically.
- **Login values come back empty** — edit `src/main/resources/credentials.properties`.
