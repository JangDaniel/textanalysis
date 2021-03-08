package com.insutil.textanalysis.common.util;

import com.insutil.textanalysis.repository.CodeRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class CodeIdUtil {
    private final CodeRepository codeRepository;

    public long getCodeIdByCodeId(final String strCodeId) {
        return codeRepository.findEnabledCodeByCodeId(strCodeId)
                .block().getId();
    }

}
