# WIFI
Add this network to `/etc/wpa_supplicant/wpa_supplicant.conf`
```
network={
    ssid="cloud-iot"
    scan_ssid=1
    psk="cloud_iot_passwd"
    mode=0
    proto=WPA2
    key_mgmt=WPA-PSK
}
```
 
 