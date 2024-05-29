package roomescape.theme.dto.response;

import roomescape.theme.model.Theme;

public record CreateThemeResponse(Long id,
                                  String name,
                                  String description,
                                  String thumbnail) {
    public static CreateThemeResponse from(final Theme theme) {
        return new CreateThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail());
    }
}