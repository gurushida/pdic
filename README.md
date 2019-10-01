# pdic
A tool to help checking password strength against dictionary attacks.

This tool allows you to compress an UTF-8 text list into a minimal automaton and quickly lookup values into this automaton.

The `build_passwd_list.sh` script will build the tool and create a compressed list from various dictionaries as well as large password lists downloaded from:

https://github.com/danielmiessler/SecLists

Here is what you get:

```
$ ls -l list.txt list.bin
-rw-r--r--  1 foo  staff  224711666 Oct  1 22:44 list.txt
-rw-r--r--  1 foo  staff   63178518 Oct  1 22:57 list.bin
$ wc -l list.txt
 19106660 list.txt
```

The resulting binary can be looked up very efficiently:

```
$ time pdic contains list.bin azertyuiop
'azertyuiop' found'

real	0m0.111s
user	0m0.079s
sys	0m0.032s
```

By comparison, here is the time it takes to look for the same password in the uncompressed list with `grep`:

```
$ time grep '^azertyuiop$' list.txt 
azertyuiop
azertyuiop

real	0m4.795s
user	0m4.769s
sys	0m0.025s
```

You can also decompress the binary list into a duplicate-free sorted text list:

```
$ time pdic decompress list.bin sorted.txt

real	0m1.946s
user	0m2.055s
sys	0m0.247s
$ wc -l sorted.txt 
 13918735 sorted.txt
```
