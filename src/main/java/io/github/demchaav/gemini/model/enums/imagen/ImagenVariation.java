/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.model.enums.imagen;

public enum ImagenVariation {
  _3_0("3.0");

  final String variation;

  ImagenVariation(String variation) {
    this.variation = variation;
  }

  @Override
  public String toString() {
    return variation;
  }
}
