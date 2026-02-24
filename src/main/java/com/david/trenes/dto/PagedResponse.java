package com.david.trenes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta estándar para listas con paginación.
 * Proporciona metadatos de paginación junto con los datos.
 *
 * @param <T> Tipo de dato contenido en la lista
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResponse<T> {

    /**
     * Lista de elementos en la página actual
     */
    private List<T> content;

    /**
     * Metadatos de paginación
     */
    private PageMetadata page;

    /**
     * Metadatos de paginación
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageMetadata {
        /**
         * Número de página actual (base 0)
         */
        private int number;

        /**
         * Tamaño de la página
         */
        private int size;

        /**
         * Total de elementos disponibles
         */
        private long totalElements;

        /**
         * Total de páginas disponibles
         */
        private int totalPages;

        /**
         * Indica si es la primera página
         */
        private boolean first;

        /**
         * Indica si es la última página
         */
        private boolean last;

        /**
         * Indica si hay página siguiente
         */
        private boolean hasNext;

        /**
         * Indica si hay página anterior
         */
        private boolean hasPrevious;
    }

    /**
     * Crea una respuesta paginada
     *
     * @param content Lista de elementos
     * @param page    Metadatos de paginación
     * @param <T>     Tipo de elementos
     * @return PagedResponse con los datos y metadatos
     */
    public static <T> PagedResponse<T> of(List<T> content, PageMetadata page) {
        return PagedResponse.<T>builder()
                .content(content)
                .page(page)
                .build();
    }

    /**
     * Crea una respuesta paginada a partir de una página de Spring Data
     *
     * @param page Página de Spring Data
     * @param <T>  Tipo de elementos
     * @return PagedResponse con los datos y metadatos convertidos
     */
    public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> page) {
        PageMetadata metadata = PageMetadata.builder()
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();

        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(metadata)
                .build();
    }
}
