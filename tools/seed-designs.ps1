param(
    [Parameter(Mandatory = $true)][string]$Folder,
    [Parameter(Mandatory = $true)][ValidateSet("nails", "tattoo", "hair")][string]$Category,
    [Parameter(Mandatory = $true)][string]$Email,
    [Parameter(Mandatory = $true)][string]$Password,
    [string]$BaseUrl = "https://serviloopservices-1.onrender.com"
)

$ErrorActionPreference = "Stop"
$tmp = Join-Path $env:TEMP "seed-designs"
New-Item -ItemType Directory -Force -Path $tmp | Out-Null

if (-not (Test-Path $Folder)) { throw "No existe la carpeta: $Folder" }
$images = Get-ChildItem $Folder -File | Where-Object { $_.Extension -match '^\.(png|jpg|jpeg|webp)$' }
if ($images.Count -eq 0) { throw "No hay imagenes (.png/.jpg/.jpeg/.webp) en $Folder" }

Write-Host "Login como $Email ..." -ForegroundColor Cyan
$loginBody = @{ email = $Email; password = $Password } | ConvertTo-Json -Compress
[IO.File]::WriteAllText("$tmp\login.json", $loginBody)
$loginResp = curl.exe -s -m 90 -X POST "$BaseUrl/auth/login" -H "Content-Type: application/json" --data-binary "@$tmp\login.json"
$login = $loginResp | ConvertFrom-Json
if (-not $login.token) { throw "Login fallo: $loginResp" }
$token = $login.token
$storeId = $login.user.storeId

Write-Host ("Cuenta OK. storeId = {0}. Subiendo {1} imagenes como '{2}' ..." -f $storeId, $images.Count, $Category) -ForegroundColor Cyan

$ok = 0
$fail = 0
foreach ($img in $images) {
    $mime = if ($img.Extension -match 'png') { "image/png" } elseif ($img.Extension -match 'webp') { "image/webp" } else { "image/jpeg" }
    $b64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes($img.FullName))
    $body = @{
        name         = $img.BaseName
        prompt       = ""
        imageBase64  = $b64
        mimeType     = $mime
        category     = $Category
    } | ConvertTo-Json -Compress
    [IO.File]::WriteAllText("$tmp\body.json", $body)

    $status = curl.exe -s -m 180 -o "$tmp\resp.json" -w "%{http_code}" -X POST "$BaseUrl/designs" `
        -H "Content-Type: application/json" -H "Authorization: Bearer $token" --data-binary "@$tmp\body.json"

    if ($status -eq "201") {
        $ok++
        Write-Host ("  [OK]   {0}" -f $img.Name) -ForegroundColor Green
    } else {
        $fail++
        $err = Get-Content "$tmp\resp.json" -Raw
        Write-Host ("  [FAIL] {0} -> {1} {2}" -f $img.Name, $status, $err) -ForegroundColor Red
    }
}

Write-Host ("Listo: {0} subidas, {1} fallidas. storeId={2}, categoria={3}" -f $ok, $fail, $storeId, $Category) -ForegroundColor Cyan
