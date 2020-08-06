#/bin/sh
java -cp dist/jidcrypt-cli.jar:lib/bcpkix-jdk15on-160.jar:lib/bcprov-jdk15on-160.jar:lib/commons-cli-1.4.jar:lib/log4j-1.2.17.jar edu.mmu.idcrypt.idsign.PrimaryCLI "$@"
