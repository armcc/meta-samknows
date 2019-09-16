# meta-samknows

This meta layer provides OpenEmbedded recipes related to the [SamKnows Router SDK](https://samknows.com/technology/sdks/router-sdk) and its dependencies:

```
boost <1.69.0 or above>
curl <latest release>
ffmpeg <4.1.4 or above>
openssl <latest release>
```

The layer can be used in two different ways. Firstly, to create an installable toolchain which provides all dependencies required to build the Router SDK. This allows the Router SDK to be built outside the OpenEmbedded build environment, but in such as way that the resulting binaries are compatibly with a target root filesystem which was created from within OpenEmbedded. Secondly, a recipe to build the Router SDK allows the Router SDK, together with all it's dependencies, to be built directly from source within the OpenEmbedded build environment.

Note that although the SamKnows Router SDK is compatible with OpenSSL 1.1.x and above, this layer also provides a recipe for OpenSSL 1.0.2 for compatibility with targets which contain other 3rd party components which are not yet compatible with the latest versions of OpenSSL.

This layer is intended to be compatible with ALL releases of OpenEmbedded from 1.6 (Daisy) onwards.

# Getting Started

```shell
git clone git://github.com/openembedded/openembedded-core.git -b sumo
git clone git://github.com/openembedded/bitbake.git openembedded-core/bitbake -b 1.38
git clone git://github.com/armcc/meta-samknows.git

source ./openembedded-core/oe-init-build-env
```

Add meta-samknows to the set of active meta layers:

```shell
bitbake-layers add-layer ../meta-samknows
```

Define persistent downloads and sstate-cache directories in local.conf (optional):

```shell
mkdir -p ${HOME}/oe
echo 'DL_DIR = "${HOME}/oe/downloads"' >> conf/local.conf
echo 'SSTATE_DIR = "${HOME}/oe/sstate-cache"' >> conf/local.conf
```

Build the installable toolchain:

```shell
bitbake sk-toolchain
```

# Alternative instructions for building with OE 1.6 (Daisy)

OE 1.6 was released in [Apr 2014](https://wiki.yoctoproject.org/wiki/Releases). It is no longer maintained upstream and now, five years later, requires various minor fixes and updates to build cleanly. The steps described below are based on the unofficial lgirdk "daisy-next" branch of openembedded-core which contains these fixes.

Note that building on recent host distro may not work (upto Ubuntu 16.04 should be OK).

```shell
git clone git://github.com/lgirdk/openembedded-core.git -b daisy-next
git clone git://github.com/openembedded/bitbake.git openembedded-core/bitbake -b 1.22
git clone git://github.com/armcc/meta-samknows.git

source ./openembedded-core/oe-init-build-env
```

Add meta-samknows to the set of active meta layers (use sed as OE 1.6 lacks "bitbake-layers add-layer"):

```shell
sed '/meta-samknows /d; /meta /a\  '$(cd ../meta-samknows && pwd)' \\' -i conf/bblayers.conf
```

Break an unnecessary default dependency on libsdl from the host:

```shell
echo 'PACKAGECONFIG_pn-qemu-native_remove = "sdl"' >> conf/local.conf
echo 'PACKAGECONFIG_pn-nativesdk-qemu_remove = "sdl"' >> conf/local.conf
sed '/libsdl-native/d' -i conf/local.conf
```

Define persistent downloads and sstate-cache directories in local.conf (optional):

```shell
mkdir -p ${HOME}/oe
echo 'DL_DIR = "${HOME}/oe/downloads"' >> conf/local.conf
echo 'SSTATE_DIR = "${HOME}/oe/sstate-cache"' >> conf/local.conf
```

Build the installable toolchain:

```shell
bitbake sk-toolchain
```

The result of building the sk-toolchain recipe will be an installable toolchain under tmp-eglibc/deploy/sdk:

```shell
ls -l tmp-eglibc/deploy/sdk/
total 126672
-rwxr-xr-x 1 andre andre 129704080 Sep 17 14:00 oecore-x86_64-i586-toolchain-nodistro.0.sh
```

To install the toolchain:

```shell
./tmp-eglibc/deploy/sdk/oecore-x86_64-i586-toolchain-nodistro.0.sh -d /opt/sk-toolchain -y
Enter target directory for SDK (default: /usr/local/oecore-x86_64): /opt/sk-toolchain
You are about to install the SDK to "/opt/sk-toolchain". Proceed[Y/n]?Y
Extracting SDK...done
Setting it up...done
SDK has been successfully set up and is ready to be used.
```

The recommended way to use the toolchain is to first source the environment-setup-XXX script in the top level of the toolchain install directory and then call the toolchain via the $CC, $OBJCOPY, etc environment variables to ensure that options such as --sysroot are set correctly:

```shell
source /opt/sk-toolchain/environment-setup-i586-oe-linux
echo $CC
i586-oe-linux-gcc -m32 -march=i586 --sysroot=/opt/sk-toolchain/sysroots/i586-oe-linux
$CC --version
i586-oe-linux-gcc (GCC) 4.8.2
Copyright (C) 2013 Free Software Foundation, Inc.
This is free software; see the source for copying conditions.  There is NO
warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
```
