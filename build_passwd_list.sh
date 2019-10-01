#!/bin/sh

# build the app
./gradlew install

# download password lists
mkdir -p lists
cd lists
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/Cracked-Hashes/milw0rm-dictionary.txt
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/Common-Credentials/10-million-password-list-top-1000000.txt
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/Common-Credentials/100k-most-used-passwords-NCSC.txt
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/xato-net-10-million-passwords.txt
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/openwall.net-all.txt
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/bt4-password.txt
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/darkc0de.txt
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/dutch_wordlist
wget https://github.com/danielmiessler/SecLists/raw/master/Passwords/mssql-passwords-nansh0u-guardicore.txt
cd ..

# put them together with dictionaries in one big list
cat lists/* delaf* > list.txt

# compress the list
pdic compress list.txt list.bin

echo
echo 'Password list compressed.'
echo 'You can now search for existing entries with:'
echo
echo '$ pdic contains list.bin <your password>'
echo
echo 'You can also decompress the binary list into a duplicate-free sorted text list with:'
echo
echo '$ pdic decompress list.bin <output>'
echo