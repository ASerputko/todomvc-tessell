
require 'gwt.rb'
require 'buildr/ivy_extension'

THIS_VERSION = ENV['version'] || 'SNAPSHOT'
Java.java.lang.System.setProperty("version", THIS_VERSION)
repositories.remote << 'http://www.ibiblio.org/maven2'

define 'todomvc' do
  project.group = 'org.gwtmpv'
  project.version = THIS_VERSION
  project.resources.from(_('src/main/java')).exclude('*.java')
  package(:war)

  ivy.compile_conf(['compile', 'provided']).test_conf('test').package_conf('compile')

  resources = FileList[_('src/main/java/org/gwtmpv/todomvc/client/resources/**/*')]
  views     = FileList[_('src/main/java/org/gwtmpv/todomvc/client/views/**/*')]
  generated_java = file(_('target/generated/java') => (views + resources)) do |dir|
    mkdir_p dir.to_s
    touch dir.to_s
    task('gwt-mpv:generate').invoke
  end

  compile.from generated_java

  task :gwt => [:compiledeps, gwt('org.gwtmpv.todomvc.TodoMvc', {
    :max_memory => '512m',
    :style => 'OBF',
    :jvmargs => ['-Djava.awt.headless=true'],
    :classpath => []
  })]

  task 'gwt-mpv:generate' => :compiledeps do
    puts "Generating gwt-mpv output..."
    Buildr.ant('gwt-mpv:generate') do |ant|
      pathid = "#{project.name}-gwt-mpv-classpath"
      ant.path :id => pathid do
        project.compile.dependencies.each do |path|
          ant.pathelement :location => path
        end
      end
      ant.java :fork => true, :failonerror => true, :classname => 'org.gwtmpv.generators.Generator' do
        ant.classpath :refid => pathid
        ant.arg :value => '--inputDirectory'
        ant.arg :value => 'src/main/java'
        ant.arg :value => '--outputDirectory'
        ant.arg :value => 'target/generated/java'
        ant.arg :value => '--viewsPackageName'
        ant.arg :value => 'org.gwtmpv.todomvc.client.views'
        ant.arg :value => '--resourcesPackageName'
        ant.arg :value => 'org.gwtmpv.todomvc.client.resources'
      end
    end
  end
end

