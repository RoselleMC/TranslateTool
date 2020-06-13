package com.iroselle.TranslateTool.bukkit.util;

import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings("all")
public class YamlLoader extends YamlConfiguration {

    private DumperOptions yamlOptions = new DumperOptions();
    private Representer yamlRepresenter = new YamlRepresenter();
    private Yaml yaml;

    public YamlLoader() {
        this.yaml = new Yaml(new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);
    }

    public void load(final File file) throws IOException, InvalidConfigurationException {
        FileInputStream stream = new FileInputStream(file);
        load((new InputStreamReader(stream, Charsets.UTF_8)));
    }

    public void load(final Reader reader) throws IOException, InvalidConfigurationException {
        BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = input.readLine()) != null) {
            builder.append(line);
            builder.append('\n');
        }
        loadFromString(builder.toString());
    }

    @SuppressWarnings("rawtypes")
    public void loadFromString(String contents) throws InvalidConfigurationException {
        Map input;
        try {
            input = (Map) yaml.load(contents);
        } catch (YAMLException var4) {
            throw new InvalidConfigurationException(var4);
        } catch (ClassCastException var5) {
            throw new InvalidConfigurationException("Top level is not a Map.");
        }
        String header = parseHeader(contents);
        if (header.length() > 0) {
            options().header(header);
        }
        if (input != null) {
            convertMapsToSections(input, this);
        }
    }

    public void save(final File file) throws IOException {
        String data = this.saveToString();
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                com.google.common.base.Charsets.UTF_8)) {
            writer.write(data);
        }
    }

    public String saveToString() {
        this.yamlOptions.setIndent(options().indent());
        this.yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String header = buildHeader();
        String dump = yaml.dump(getValues(false));
        if (dump.equals("{}\n")) {
            dump = "";
        }
        return header + dump;
    }

    public static YamlLoader loadConfiguration(File file) {
        YamlLoader config = new YamlLoader();
        try {
            config.load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, var4);
        }
        return config;
    }
}
