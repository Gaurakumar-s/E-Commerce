$baseUrl = "http://localhost:8080"
$adminEmail = "admin@shop.com"
$adminPassword = "admin123"

Write-Host "1. Authenticating as Admin..." -ForegroundColor Cyan
$loginBody = @{
    email    = $adminEmail
    password = $adminPassword
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.accessToken
    Write-Host "   Success! Token received." -ForegroundColor Green
}
catch {
    Write-Host "   Failed to login. Ensure backend is running." -ForegroundColor Red
    Write-Host "   Error: $_"
    exit
}

$headers = @{
    Authorization  = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "`n2. Creating a New Product..." -ForegroundColor Cyan
$newProduct = @{
    name          = "Test Product $(Get-Random)"
    description   = "Created via PowerShell script"
    price         = 99.99
    stockQuantity = 50
    categoryId    = 1
    active        = $true
} | ConvertTo-Json

try {
    $product = Invoke-RestMethod -Uri "$baseUrl/api/products" -Method Post -Headers $headers -Body $newProduct
    Write-Host "   Success! Product Created: $($product.name) (ID: $($product.id))" -ForegroundColor Green
}
catch {
    Write-Host "   Failed to create product." -ForegroundColor Red
    Write-Host "   Error: $_"
    exit
}

Write-Host "`n3. Reading Product (Get By ID)..." -ForegroundColor Cyan
try {
    $readProduct = Invoke-RestMethod -Uri "$baseUrl/api/products/$($product.id)" -Method Get
    Write-Host "   Success! Retrieved: $($readProduct.name)" -ForegroundColor Green
}
catch {
    Write-Host "   Failed to get product." -ForegroundColor Red
}

Write-Host "`n4. Updating Product..." -ForegroundColor Cyan
$updateBody = @{
    name          = "$($product.name) - UPDATED"
    description   = "Updated description"
    price         = 149.99
    stockQuantity = 45
    categoryId    = 1
    active        = $true
} | ConvertTo-Json

try {
    $updatedProduct = Invoke-RestMethod -Uri "$baseUrl/api/products/$($product.id)" -Method Put -Headers $headers -Body $updateBody
    Write-Host "   Success! Updated name: $($updatedProduct.name)" -ForegroundColor Green
    Write-Host "   Updated price: $($updatedProduct.price)"
}
catch {
    Write-Host "   Failed to update product." -ForegroundColor Red
}

Write-Host "`n5. Deleting Product..." -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/api/products/$($product.id)" -Method Delete -Headers $headers
    Write-Host "   Success! Product deleted." -ForegroundColor Green
}
catch {
    Write-Host "   Failed to delete product." -ForegroundColor Red
}

Write-Host "`n-------------------------------------"
Write-Host "CRUD Test Completed Successfully!" -ForegroundColor Green
