docker run -it --rm -v %CD%:/app -v "%USERPROFILE%/.m2:/root/.m2" -w /app maven:3.9.6-eclipse-temurin-17-focal mvn -T 1.5C -U -B clean -Dmaven.test.skip=true -P windows-linux install
