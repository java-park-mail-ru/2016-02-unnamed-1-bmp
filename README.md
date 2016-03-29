# 2016-02-unnamed-1-bmp Sea Battle

## Usage
Step 1. Install: Intelij Idea 15, JDK 8, maven, mysql >= 5.1, h2(for tests)

Step 2. Clone the repo:
```
git clone https://github.com/java-park-mail-ru/2016-02-unnamed-1-bmp.git
```
Step 3. Open the project in IJ. Mark folders setups and scr as sources.

Step 4. Build project from root directory
```
mvn compile assembly:single
```
Step 5. Start mysql server and run project:
```
java -jar target/seaFight-1.0-jar-with-dependencies.jar
```
