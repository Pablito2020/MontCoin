# Key and certificates folder

Create a private key with:

```bash
$ openssl genrsa -out private_key.pem 2048
```

Derive a public key with:

```bash
$ openssl rsa -in private_key.pem -pubout -out public.crt
```
