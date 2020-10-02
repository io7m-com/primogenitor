/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.primogenitor.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class TrivialJavadoc
{
  private static final Logger LOG = Logger.getLogger(TrivialJavadoc.class.getCanonicalName());

  private TrivialJavadoc()
  {

  }

  // CHECKSTYLE:OFF
  public static void main(
    final String[] args)
    throws Exception
  {
    // CHECKSTYLE:ON
    if (args.length != 5) {
      throw new IOException(
        "usage: source-directory classpath-file output-directory log-file options-file");
    }

    final var sources =
      Paths.get(args[0]).toAbsolutePath();
    final var classpathFile =
      Paths.get(args[1]).toAbsolutePath();
    final var outputDirectory =
      Paths.get(args[2]).toAbsolutePath();
    final var logFile =
      Paths.get(args[3]).toAbsolutePath();
    final var optionsFile =
      Paths.get(args[4]).toAbsolutePath();

    LOG.info("source-directory: " + sources);
    LOG.info("classpath-file:   " + classpathFile);
    LOG.info("output-directory: " + outputDirectory);
    LOG.info("log-file:         " + logFile);
    LOG.info("options-file:     " + optionsFile);

    final List<String> sourceFiles;
    try (Stream<Path> sourceStream = Files.walk(sources)) {
      sourceFiles =
        sourceStream
          .map(Path::toAbsolutePath)
          .filter(Files::isRegularFile)
          .filter(TrivialJavadoc::isJavaSourceFile)
          .map(Path::toString)
          .sorted()
          .collect(Collectors.toList());
    }

    try (var writer = Files.newBufferedWriter(optionsFile, UTF_8)) {
      writer.append("-notimestamp");
      writer.newLine();
      writer.append("-d");
      writer.newLine();
      writer.append(outputDirectory.toAbsolutePath().toString());
      writer.newLine();
      writer.append("--class-path");
      writer.newLine();
      writer.append(Files.readString(classpathFile));
      writer.newLine();

      for (final var sourceFile : sourceFiles) {
        writer.append(sourceFile);
        writer.newLine();
      }
    }

    final var commandParameters = new ArrayList<String>();
    commandParameters.add("javadoc");
    commandParameters.add("@" + optionsFile);

    LOG.info("exec " + String.join(" ", commandParameters));

    final var process =
      new ProcessBuilder(commandParameters)
        .redirectError(logFile.toFile())
        .redirectOutput(logFile.toFile())
        .start();

    final var status = process.waitFor();
    if (status != 0) {
      Files.readAllLines(logFile).forEach(System.err::println);
      throw new IOException("JavaDoc command exited with status " + status);
    }

    applyOverviewWorkaround(outputDirectory);
    applySearchIndicesWorkaround(outputDirectory);
  }

  /**
   * The following files are produced by the JavaDoc tool, and it produces
   * non-reproducible archives.
   */

  private static void applySearchIndicesWorkaround(
    final Path outputDirectory)
    throws IOException
  {
    final var problematicFiles =
      List.of(
        "member-search-index.zip",
        "package-search-index.zip",
        "type-search-index.zip"
      );

    for (final var file : problematicFiles) {
      final Path outputFile =
        outputDirectory.toAbsolutePath().resolve(file);
      final Path outputFileTmp =
        outputDirectory.toAbsolutePath().resolve(file + ".tmp");

      LOG.info("strip " + outputFile);
      if (Files.isRegularFile(outputFile)) {
        ReproducibleZip.makeReproducible(outputFile, outputFileTmp);
      }
    }
  }

  /**
   * The overview-summary file is just a redirect to the index.html file,
   * but unfortunately ignores the -notimestamp flag, causing the output
   * to be non-reproducible.
   */

  private static void applyOverviewWorkaround(
    final Path outputDirectory)
    throws IOException
  {
    Files.copy(
      outputDirectory.toAbsolutePath().resolve("index.html"),
      outputDirectory.toAbsolutePath().resolve("overview-summary.html"),
      StandardCopyOption.REPLACE_EXISTING
    );
  }

  private static boolean isJavaSourceFile(
    final Path path)
  {
    return path.getFileName().toString().endsWith(".java");
  }
}
