#!/bin/bash
set -e

awslocal s3 mb s3://taskfleet-imports || true

awslocal sqs create-queue --queue-name taskfleet-import-jobs || true
awslocal sqs create-queue --queue-name taskfleet-import-jobs-dlq || true
