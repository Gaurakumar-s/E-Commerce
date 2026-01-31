# Maven Setup Script for Windows
# Run this script as Administrator for permanent setup, or normally for current session

Write-Host "=== Maven Setup Script ===" -ForegroundColor Cyan
Write-Host ""

# Find Java installation
Write-Host "1. Finding Java installation..." -ForegroundColor Yellow
try {
    $javaPath = (Get-Command java).Source | Split-Path | Split-Path
    Write-Host "   ✓ Java found at: $javaPath" -ForegroundColor Green
    
    # Set JAVA_HOME for current session
    $env:JAVA_HOME = $javaPath
    Write-Host "   ✓ JAVA_HOME set for current session" -ForegroundColor Green
} catch {
    Write-Host "   ✗ Java not found in PATH" -ForegroundColor Red
    Write-Host "   Please install Java or add it to PATH" -ForegroundColor Red
    exit 1
}

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if ($isAdmin) {
    Write-Host ""
    Write-Host "2. Setting JAVA_HOME permanently..." -ForegroundColor Yellow
    try {
        [System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaPath, "Machine")
        Write-Host "   ✓ JAVA_HOME set permanently" -ForegroundColor Green
    } catch {
        Write-Host "   ✗ Failed to set JAVA_HOME permanently" -ForegroundColor Red
    }
} else {
    Write-Host ""
    Write-Host "2. Running as regular user (not Administrator)" -ForegroundColor Yellow
    Write-Host "   JAVA_HOME set for current session only" -ForegroundColor Yellow
    Write-Host "   Run as Administrator to set permanently" -ForegroundColor Yellow
}

# Check for Maven
Write-Host ""
Write-Host "3. Checking for Maven..." -ForegroundColor Yellow

# Common Maven installation paths
$mavenPaths = @(
    "C:\Program Files\Apache\maven",
    "C:\apache-maven",
    "$env:USERPROFILE\apache-maven",
    "$env:ProgramFiles\Apache\maven"
)

$mavenFound = $false
foreach ($path in $mavenPaths) {
    if (Test-Path "$path\bin\mvn.cmd") {
        $mavenPath = $path
        $mavenFound = $true
        break
    }
}

if ($mavenFound) {
    Write-Host "   ✓ Maven found at: $mavenPath" -ForegroundColor Green
    
    # Add to PATH for current session
    if ($env:Path -notlike "*$mavenPath\bin*") {
        $env:Path += ";$mavenPath\bin"
        Write-Host "   ✓ Maven added to PATH for current session" -ForegroundColor Green
    }
    
    if ($isAdmin) {
        try {
            $currentPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
            if ($currentPath -notlike "*$mavenPath\bin*") {
                [System.Environment]::SetEnvironmentVariable("Path", "$currentPath;$mavenPath\bin", "Machine")
                Write-Host "   ✓ Maven added to PATH permanently" -ForegroundColor Green
            }
        } catch {
            Write-Host "   ✗ Failed to set PATH permanently" -ForegroundColor Red
        }
    }
} else {
    Write-Host "   ✗ Maven not found" -ForegroundColor Red
    Write-Host ""
    Write-Host "   To install Maven:" -ForegroundColor Yellow
    Write-Host "   1. Download from: https://maven.apache.org/download.cgi" -ForegroundColor White
    Write-Host "   2. Extract to: C:\Program Files\Apache\maven" -ForegroundColor White
    Write-Host "   3. Run this script again as Administrator" -ForegroundColor White
    Write-Host ""
}

# Verify installation
Write-Host ""
Write-Host "4. Verifying installation..." -ForegroundColor Yellow

if (Get-Command java -ErrorAction SilentlyContinue) {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "   ✓ Java: $javaVersion" -ForegroundColor Green
} else {
    Write-Host "   ✗ Java not in PATH" -ForegroundColor Red
}

if (Get-Command mvn -ErrorAction SilentlyContinue) {
    $mvnVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "   ✓ Maven: $mvnVersion" -ForegroundColor Green
    Write-Host ""
    Write-Host "=== Setup Complete! ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can now run:" -ForegroundColor Cyan
    Write-Host "  mvn clean compile    - Compile the project" -ForegroundColor White
    Write-Host "  mvn clean test       - Run tests" -ForegroundColor White
    Write-Host "  mvn spring-boot:run  - Run the application" -ForegroundColor White
} else {
    Write-Host "   ✗ Maven not in PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "   If you just installed Maven, please:" -ForegroundColor Yellow
    Write-Host "   1. Restart this PowerShell window" -ForegroundColor White
    Write-Host "   2. Or run: `$env:Path += `";C:\Program Files\Apache\maven\bin`"" -ForegroundColor White
}

Write-Host ""
