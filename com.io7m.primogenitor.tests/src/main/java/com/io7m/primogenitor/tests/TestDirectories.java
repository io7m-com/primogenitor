package com.io7m.primogenitor.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.UUID;

public final class TestDirectories
{
  private TestDirectories()
  {

  }

  public static Path createBaseDirectory()
    throws IOException
  {
    final var path =
      Path.of(System.getProperty("java.io.tmpdir")).resolve("primogenitor");
    Files.createDirectories(path);
    return path;
  }

  public static Path createTempDirectory()
    throws IOException
  {
    final var path = createBaseDirectory();
    final var temp = path.resolve(UUID.randomUUID().toString());
    Files.createDirectories(temp);
    return temp;
  }

  public static Path resourceOf(
    final Class<?> clazz,
    final Path output,
    final String name)
    throws IOException
  {
    final var internal = String.format("/com/io7m/primogenitor/tests/%s", name);
    final var url = clazz.getResource(internal);
    if (url == null) {
      throw new NoSuchFileException(internal);
    }

    final var target = output.resolve(name);
    try (var stream = url.openStream()) {
      Files.copy(stream, target);
    }
    return target;
  }
}
