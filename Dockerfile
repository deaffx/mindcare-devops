# ============================================================================
# Dockerfile - MindCare Application
# Imagem oficial: Eclipse Temurin (OpenJDK da Eclipse Foundation)
# Execução: Usuário não-root (spring:spring) para segurança
# ============================================================================

FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="MindCare Team"
LABEL description="Sistema de acompanhamento de saúde mental e metas pessoais - MindCare"
LABEL version="1.0"

WORKDIR /app

# Criar usuário não-root para segurança (container NÃO roda como root)
RUN addgroup -S spring && adduser -S spring -G spring

# Copia o JAR que foi baixado no pipeline para o contexto do docker build
# Use um nome genérico (coringa) para evitar hardcode de versão:
COPY *.jar app.jar

# Mudar ownership para usuário spring
RUN chown -R spring:spring /app

# Trocar para usuário não-root
USER spring:spring

# Expor porta da aplicação
EXPOSE 8080

# Configurar JVM para container
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
