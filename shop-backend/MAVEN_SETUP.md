# Maven Setup Guide for Windows

## ✅ Prerequisites Check
- ✅ Java 21 installed (OpenJDK 21.0.9)
- ⚠️ JAVA_HOME not set (we'll set this)
- ❌ Maven not installed

---

## 📥 Step 1: Download Maven

1. **Go to**: https://maven.apache.org/download.cgi
2. **Download**: `apache-maven-3.9.x-bin.zip` (latest stable version)
3. **Extract** to: `C:\Program Files\Apache\maven` (or your preferred location)

---

## ⚙️ Step 2: Set Environment Variables

### Option A: Using PowerShell (Temporary - Current Session Only)
```powershell
# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"  # Adjust path if different
$env:Path += ";C:\Program Files\Apache\maven\bin"
```

### Option B: Using System Properties (Permanent)

1. **Open System Properties**:
   - Press `Win + X` → System
   - Or: Right-click "This PC" → Properties → Advanced system settings

2. **Set JAVA_HOME**:
   - Click "Environment Variables"
   - Under "System variables", click "New"
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-21` (or your Java installation path)
   - Click OK

3. **Set MAVEN_HOME**:
   - Click "New" again
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Program Files\Apache\maven` (where you extracted Maven)
   - Click OK

4. **Update PATH**:
   - Find "Path" in System variables
   - Click "Edit"
   - Click "New" and add: `%MAVEN_HOME%\bin`
   - Click OK on all dialogs

5. **Restart PowerShell/Terminal** for changes to take effect

---

## ✅ Step 3: Verify Installation

Open a **new** PowerShell window and run:

```powershell
# Check Java
java -version

# Check Maven
mvn -version

# Should show:
# Apache Maven 3.9.x
# Maven home: C:\Program Files\Apache\maven
# Java version: 21.0.9
```

---

## 🚀 Step 4: Test with Your Project

```powershell
cd C:\Users\ashmi\Desktop\SpringBoot_Project\shop-backend

# Clean and compile
mvn clean compile

# Run tests
mvn clean test

# Run application
mvn spring-boot:run
```

---

## 🔧 Quick Setup Script (PowerShell)

Run this in PowerShell **as Administrator**:

```powershell
# Find Java installation
$javaPath = (Get-Command java).Source | Split-Path | Split-Path
$env:JAVA_HOME = $javaPath

# Set JAVA_HOME permanently
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaPath, "Machine")

# Download and setup Maven (if not already done)
# You'll need to manually download and extract Maven first, then:
$mavenPath = "C:\Program Files\Apache\maven"
$env:MAVEN_HOME = $mavenPath
[System.Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenPath, "Machine")

# Add to PATH
$currentPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
if ($currentPath -notlike "*$mavenPath\bin*") {
    [System.Environment]::SetEnvironmentVariable("Path", "$currentPath;$mavenPath\bin", "Machine")
}

Write-Host "Maven setup complete! Please restart your terminal."
```

---

## 🐛 Troubleshooting

### Issue: "mvn is not recognized"
- **Solution**: Restart PowerShell/terminal after setting PATH
- Verify PATH: `echo $env:Path` (should include Maven bin directory)

### Issue: "JAVA_HOME not set"
- **Solution**: Set JAVA_HOME to your Java installation directory
- Find Java: `where java` then navigate up to the JDK folder

### Issue: "Maven version shows Java 17 but you have Java 21"
- **Solution**: Ensure JAVA_HOME points to Java 21 installation

---

## 📝 Alternative: Use Maven Wrapper (Recommended for Projects)

Instead of installing Maven globally, you can use Maven Wrapper which comes with the project:

```powershell
# Generate Maven Wrapper (if not present)
mvn wrapper:wrapper

# Then use:
.\mvnw.cmd clean compile
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

This ensures everyone uses the same Maven version! 🎯
