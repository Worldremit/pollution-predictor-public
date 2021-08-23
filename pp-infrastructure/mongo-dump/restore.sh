#!/usr/bin/env sh

command -v 7z >/dev/null 2>&1 || { echo >&2 "7zip is required. Aborting."; exit 1; }
command -v mongorestore >/dev/null 2>&1 || { echo >&2 "mongodb-tools (mongorestore) are required. Aborting."; exit 1; }

7z x pp_2010-2020.7z -so | mongorestore --archive --drop
