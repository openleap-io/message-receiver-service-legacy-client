# message-receiver-service-legacy-client

message-receiver-service-legacy-client

# Usage

In build folder extract the zip.

## Sending an email

Navigate to `mailsend-v3.exe`

Run:

```run
mailsend-v3.exe C:\path\to\file.cfg
```

## Override the to,cc,bcc

Set the system variable `MAILSEND`

```run
export MAILSEND=some_email@company.com
```

Then run:

```run
mailsend-v3.exe C:\path\to\file.cfg
```

any `to`, `cc`, `bcc` in the file will be overridden by the system variable.

## Override the authentication server and mail receiver service settings

Set the system variable `MAILSEND_CONFIG`

```run
export MAILSEND_CONFIG=C:\path\to\file.cfg
```

the program will use the configuration file specified in the system variable.

Example of the configuration file:

```cfg
base-url: https://uniapitest.datapart-factoring.de/gw
token-url: https://idptest.datapart-factoring.de/realms/master/protocol/openid-connect/token
client-id: message-receiver-service-client
client-secret: ***
```