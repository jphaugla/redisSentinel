#!/bin/bash
PEM_DIR=sentinel_tests/tls
rm -f ${PEM_DIR}/*
openssl genrsa -out ${PEM_DIR}/ca-private-key.pem 2048
openssl rsa -in ${PEM_DIR}/ca-private-key.pem -pubout -out ${PEM_DIR}/ca-public-key.pem
openssl req -new -x509 -key ${PEM_DIR}/ca-private-key.pem -out ${PEM_DIR}/CA-cert.pem -days 1000
openssl req -out ${PEM_DIR}/sslcert.csr -newkey rsa:2048 -nodes -keyout ${PEM_DIR}/private.key -config san.cnf
openssl x509 -req -days 365 -in ${PEM_DIR}/sslcert.csr -CA ${PEM_DIR}/CA-cert.pem  -CAkey ${PEM_DIR}/ca-private-key.pem  -CAcreateserial -out ${PEM_DIR}/san.crt
