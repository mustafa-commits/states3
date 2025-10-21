# المرحلة الأولى: بناء التطبيق باستخدام Maven
FROM maven:3.9.6-eclipse-temurin-22 AS build

# تعيين مجلد العمل داخل الحاوية
WORKDIR /app

# نسخ جميع ملفات المشروع (pom.xml و src)
COPY . .

# بناء التطبيق باستخدام Maven
RUN mvn clean package -DskipTests

# المرحلة الثانية: بناء الصورة النهائية باستخدام OpenJDK 22
FROM eclipse-temurin:22-jre-jammy

# تعيين مجلد العمل داخل الحاوية
WORKDIR /app

# نسخ التطبيق المبني من المرحلة الأولى
COPY --from=build /app/target/realState.jar realState.jar

# تعيين المنفذ الذي يستخدمه التطبيق
EXPOSE 8080

# تشغيل التطبيق
ENTRYPOINT ["java", "-jar", "realState.jar"]
