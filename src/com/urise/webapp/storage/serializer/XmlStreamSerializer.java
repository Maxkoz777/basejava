package com.urise.webapp.storage.serializer;

import com.urise.webapp.model.*;
import com.urise.webapp.util.XmlParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class XmlStreamSerializer implements StreamSerializer{
    private XmlParser parser;

    public XmlStreamSerializer() {
        this.parser = new XmlParser(
                Resume.class, Organization.class, Link.class,
                OrganizationSection.class, TextSection.class, ListSection.class,
                Organization.Position.class);
    }


    @Override
    public void doWrite(Resume resume, OutputStream outputStream) throws IOException {
        try(Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)){
            parser.marshall(resume, writer);
        }
    }

    @Override
    public Resume doRead(InputStream inputStream) throws IOException {
        try(Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)){
            return parser.unmarshall(reader);
        }
    }
}
