@echo off
set GPG_USERNAME=%1
set GPG_PASSWORD=%2
set FILE_TO_SIGN=%3
echo %GPG_PASSWORD%|C:\Programmi\GNU\GnuPG\gpg.exe --batch --passphrase-fd 0 -u %GPG_USERNAME% -b -a -s "%FILE_TO_SIGN%"
