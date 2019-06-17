package com.hm.seckill.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {IsMobileValidator.class}
)

// 自定义校验器
public @interface IsMobile {

    boolean required() default true; // 这个参数必须要有

    String message() default "手机号码格式错误"; // 校验错误显示的信息

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}