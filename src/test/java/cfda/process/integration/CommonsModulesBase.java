package cfda.process.integration;

import cfda.util.LogFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 * Definition von Modulen, welche in Tests eingewoben werden können.
 * <p/>
 * Achtung:
 * Bitte in der Definition von Modulen keine Packages verwenden, da sonst Sourcen aus Test-Packages einfließen!
 */
public class CommonsModulesBase {

    private static MavenDependencyResolver resolverX;

    private static MavenDependencyResolver resolver() {
        if (resolverX == null) {
            resolverX = DependencyResolvers.use(MavenDependencyResolver.class)
                    //.goOffline()
                    .loadMetadataFromPom("pom.xml");
        }
        return resolverX;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static Archive<?> removeClass(Archive<?> target, Class<?> subject) {
        target.delete(subject.getCanonicalName().replace('.', '/') + ".class");
        return target;
    }

    /**
     * @param target the target to enrich with fox deps
     * @return Abhängigkeiten der FOX engine. Diese enthält auch den Fox Client zur Ausführung von Prozessen in Activiti.
     */
    public static WebArchive foxEngineDependencies(WebArchive target) {
        // MavenResolutionFilter filter = new ScopeFilter("compile");
        // Collection<JavaArchive> javaArchives = resolver().resolveAs(JavaArchive.class, filter);
        return target
                .addAsResource("META-INF/beans.xml")
                .addClass(LogFactory.class)
                .addAsWebResource("META-INF/processes.xml", "WEB-INF/classes/META-INF/processes.xml")
                .addAsLibraries(resolver().artifact("org.camunda.bpm:camunda-engine-cdi").resolveAsFiles())
                .addAsLibraries(resolver().artifact("org.camunda.bpm.javaee:camunda-ejb-client").resolveAsFiles())
                .addAsLibraries(resolver().artifact("org.camunda.bpm.incubation:camunda-bpm-fluent-assertions").resolveAsFiles())
                .addAsLibraries(resolver().artifact("org.camunda.bpm.incubation:camunda-bpm-fluent-engine-api").resolveAsFiles())
                .addAsLibraries(resolver().artifact("org.mockito:mockito-all").resolveAsFiles());

    }

}