SUMMARY = "Installable toolchain containing build dependencies for SamKnows Router SDK"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit populate_sdk

TOOLCHAIN_TARGET_TASK_append = " \
    boost \
    curl \
    ffmpeg \
    openssl \
"
