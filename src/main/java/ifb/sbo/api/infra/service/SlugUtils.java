package ifb.sbo.api.infra.service;

import java.text.Normalizer;

public class SlugUtils {

    public static String toSlug(String texto) {
        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return normalized
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^\\w\\s-]", "")
                .trim()
                .replaceAll("[\\s_]+", "-")
                .toLowerCase();
    }
}
