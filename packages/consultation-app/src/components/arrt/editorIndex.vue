<template>
    <van-form>
        <div class='entry-group-baseinfo' v-for='(info,index) in currentFields' :key="index">
            <!--单行文本-->
            <div
                    class='bot-border'
                    style='position: unset !important;'
                    v-if='(info.widgetType === Constants.CustomFormWidgetType.SingleLineText
                        || info.widgetType === Constants.CustomFormWidgetType.FullName) &&
                     info.exactType !== Constants.FieldExactType.Phone &&
                     info.exactType !== Constants.FieldExactType.SCR &&
                     info.exactType !== Constants.FieldExactType.BMI &&
                     info.exactType !== Constants.FieldExactType.GFR &&
                     info.exactType !== Constants.FieldExactType.CCR &&
                     info.exactType !== Constants.FieldExactType.CourseOfDisease'>
                <van-field
                        v-model='info.values[0].attrValue'
                        :placeholder='info.placeholder'
                        :name='info.label'
                        :required='info.required'
                        :label='info.label'
                        style='padding: 13px 16px;'
                        error-message-align="right"
                        @change="() => infoValueChange(info)"
                        :error-message="info.errorMessage"
                        :maxlength='info.maxEnterNumber ? info.maxEnterNumber : 120'>
                    <div slot='button' v-if='info.rightUnit'>{{ info.rightUnit }}</div>
                </van-field>
                <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
            </div>
            <!--scr-->
            <div
                    class='bot-border'
                    style='position: unset !important;'
                    v-if='info.exactType === Constants.FieldExactType.SCR'>
                <van-field
                        v-model='info.values[0].attrValue'
                        :placeholder='info.placeholder'
                        :name='info.label'
                        :required='info.required'
                        type='number'
                        :label='info.label'
                        error-message-align="right"
                        @change="() => infoValueChange(info)"
                        :error-message="info.errorMessage"
                        style='padding: 13px 16px;'>
                    <div v-if='info.rightUnit' slot='button'>{{ info.rightUnit }}</div>
                </van-field>
                <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
            </div>
            <!--BMI-->
            <div
                    v-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.BMI'
                    class='bot-border' style='position: unset !important;'>
                <van-cell
                        title-class='van-cell-width50'
                        label-class='van-cell-width50'
                        :value='bmi(index)'>
                    <template slot="title">
                        <div class='van-cell-div'>{{ info.label }}</div>
                    </template>
                    <template slot="label">
                        <div class='van-cell-label'>{{ info.labelDesc }}</div>
                    </template>
                </van-cell>
            </div>
            <!-- GFR -->
            <div
                    v-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.GFR'
                    class='bot-border' style='position: unset !important;'>
                <van-cell
                        title-class='van-cell-width50'
                        label-class='van-cell-width50'
                        :value='gfr(index)'>
                    <template slot="title">
                        <div class='van-cell-div'>{{ info.label }}</div>
                    </template>
                    <template slot="label">
                        <div class='van-cell-label'>{{ info.labelDesc }}</div>
                    </template>
                </van-cell>
            </div>
            <!-- ccr -->
            <div
                    v-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.CCR'
                    class='bot-border' style='position: unset !important;'>
                <van-cell
                        title-class='van-cell-width50'
                        label-class='van-cell-width50'
                        :value='ccr(index)'>
                    <template slot="title">
                        <div class='van-cell-div'>{{ info.label }}</div>
                    </template>
                    <template slot="label">
                        <div class='van-cell-label'>{{ info.labelDesc }}</div>
                    </template>
                </van-cell>
            </div>
            <!-- ckd -->
            <div
                    v-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.CourseOfDisease'
                    class='bot-border' style='position: unset !important;'>
                <van-cell
                        title-class='van-cell-width50'
                        label-class='van-cell-width50'
                        :value='ckd(index)'>
                    <template slot="title">
                        <div class='van-cell-div'>{{ info.label }}</div>
                    </template>
                    <template slot="label">
                        <div class='van-cell-label'>{{ info.labelDesc }}</div>
                    </template>
                </van-cell>
            </div>
            <!-- 手机号 -->
            <div
                    class='bot-border'
                    v-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText
                    && info.exactType === Constants.FieldExactType.Phone'>
                <van-field
                        v-model='info.values[0].attrValue'
                        :placeholder='info.placeholder'
                        :name='info.label'
                        :required='info.required'
                        :label='info.label'
                        type='number'
                        style='padding: 13px 16px;'
                        error-message-align="right"
                        @change="() => infoValueChange(info)"
                        :error-message="info.errorMessage"
                        :maxlength='info.maxEnterNumber ? info.maxEnterNumber : 11'>
                    <div slot='button' v-if='info.rightUnit'>{{ info.rightUnit }}</div>
                </van-field>
                <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
            </div>
            <!--多行文本-->
            <div
                    class='bot-border'
                    v-else-if='info.widgetType === Constants.CustomFormWidgetType.MultiLineText'>
                <van-field
                        style='padding: 13px 16px;'
                        v-model='info.values[0].attrValue'
                        rows='2'
                        :label='info.label'
                        :required='info.required'
                        :name='info.label'
                        type='textarea'
                        autosize
                        error-message-align="right"
                        @change="() => infoValueChange(info)"
                        :placeholder='info.placeholder'
                        :error-message="info.errorMessage"
                        :maxlength='info.maxEnterNumber ? info.maxEnterNumber : 450'
                        show-word-limit
                />
                <div v-if='info.labelDesc' class='desc'>{{ info.labelDesc }}</div>
            </div>
            <!--数字   是否為必填-->
            <div
                    class='bot-border'
                    v-if='info.widgetType === Constants.CustomFormWidgetType.Number'>
                <van-field
                        v-if='info.canDecimal && info.canDecimal === 1'
                        v-model='info.values[0].attrValue'
                        :placeholder='info.placeholder'
                        :name='info.label'
                        :label='info.label'
                        :required='info.required'
                        type='number'
                        style='padding: 13px 16px;'
                        error-message-align="right"
                        @change="() => infoValueChange(info)"
                        :error-message="info.errorMessage"
                        :maxlength='info.maxValue && info.maxValue > 0 ? info.maxValue : 450'>
                    <div slot='button' v-if='info.rightUnit'>{{ info.rightUnit }}</div>
                </van-field>
                <van-field
                        v-else
                        v-model='info.values[0].attrValue'
                        :placeholder='info.placeholder'
                        :name='info.label'
                        :label='info.label'
                        :required='info.required'
                        type='digit'
                        style='padding: 13px 16px;'
                        error-message-align="right"
                        @change="() => infoValueChange(info)"
                        :error-message="info.errorMessage"
                        :maxlength='info.maxValue && info.maxValue > 0 ? info.maxValue : 450'>
                    <div slot='button' v-if='info.rightUnit'>{{ info.rightUnit }}</div>
                </van-field>
                <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
            </div>
            <!--单选框-->
            <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio'>
                <div class='bot-border'>
                    <van-field :label="info.label" readonly :required="info.required" style="padding:10px 16px!important"></van-field>
                    <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
                    <van-field style="padding:0px 16px 10px 16px!important" :error-message="info.errorMessage" error-message-align="right">
                        <template slot="input">
                            <van-radio-group v-model="info.values[0].attrValue" class="attr-radio-group"
                                            @change="(value) => radioClick(value, info)">
                                             <!-- @change="(value) => radioClick(value, info)" -->
                                <van-row v-for="(option,k) in info.options" :key="k">
                                    <van-col span="24">
                                        <van-radio class="radio-item-padding option-word-wrap" :name="option.id">
                                            {{ option.attrValue }}
                                            <!-- <template slot="props"> -->
                                                <!-- <i class="icon iconfont icondanxuanweixuan theme-color"
                                                   v-if="!option.checked"></i>
                                                <svg class="icon svg-icon" style="width: 16px; height: 20px"
                                                     aria-hidden="true" v-else>
                                                    <use xlink:href="#icondanxuanxuanzhong"></use>
                                                </svg> -->
                                            <!-- </template> -->
                                        </van-radio>
                                    </van-col>
                                    <van-col span="24">
                                        <!-- 选中的项不是其他选项 -->
                                        <van-field
                                                v-show="info.itemAfterHasEnter === 1 && info.values[0].valueText && info.values[0].valueText !== '其他' && option.id === info.values[0].attrValue"
                                                v-model='option.desc'
                                                @change="(value) => {setValueDesc(info.values, option, info)}"
                                                placeholder='请输入选项备注'
                                                :required='info.itemAfterHasEnterRequired === 1'
                                                style='padding: 13px 16px;'
                                                show-word-limit
                                                :maxlength='info.itemAfterHasEnterSize ? info.itemAfterHasEnterSize : 120'
                                                :error-message="option.errorMessage"
                                                error-message-align="right"
                                        >
                                        </van-field>
                                    </van-col>
                                </van-row>
                            </van-radio-group>
                        </template>
                    </van-field>

                    <div style='width: 100%;'>
                        <van-field
                                v-if='hasOther(info.values)'
                                v-model='info.otherValue'
                                rows='2'
                                :required='1 === info.otherEnterRequired '
                                type='textarea'
                                autosize
                                label="备注"
                                placeholder='请输入其他选项的备注'
                                :maxlength='info.otherValueSize ? info.otherValueSize : 120'
                                show-word-limit
                                @change="() => {1 === info.otherEnterRequired && info.otherValue && info.otherValue.length > 0 ? info.otherErrorMessage = '' : info.otherErrorMessage = '请输入其他选项的备注'}"
                                error-message-align="right"
                                :error-message="info.otherErrorMessage"
                        />
                    </div>
                    <div v-for='(value,k) in info.values' :key="k">
                        <index-item :question='value.questions'></index-item>
                    </div>
                </div>
            </div>
            <!-- 下拉框 -->
            <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.DropdownSelect'>
                <div class='bot-border'>
                    <van-row>
                        <van-col span="24" style="min-height: 0 !important;">
                            <van-cell
                                    title-class="van-cell-width50"
                                    label-class="van-cell-width50"
                                    :required='info.required'
                                    :clickable='true'
                                    
                                    >
                                <template slot="title">
                                    <div>{{ info.label }}</div>
                                </template>
                                <!-- :style="{display:info.values[0].attrValue?'':'inline-flex',justifyContent: info.values[0].attrValue?'':'space-between'}" -->
                                <div>
                                    <p style="display: inline-block;vertical-align: middle;" v-if="!info.values[0].attrValue">{{info.placeholder}}</p>
                                    <van-dropdown-menu :style="{display:info.values[0].attrValue?'':'inline-block',verticalAlign: info.values[0].attrValue?'':'middle'}" class="van-dropdown-menu__bar--opened" >
                                        <van-dropdown-item  v-model='info.values[0].attrValue'
                                                       :options='resolveOptions(info.options)'
                                                       :closed='itemChage(info.values, info.options, info)'/>
                                                       
                                    </van-dropdown-menu>
                                </div>
                                
                            </van-cell>
                        </van-col>
                        <van-col span="24" style="min-height: 0 !important;" v-if="info.errorMessage">
                            <div class="desc"
                                 style="background-color: white !important; color: red !important; text-align: right">
                                请选择选项
                            </div>
                        </van-col>
                        <van-col span="24" style="min-height: 0 !important;">
                            <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
                        </van-col>
                    </van-row>
                    <div v-for='(value,k) in info.values' :key="k">
                        <index-item :question='value.questions'></index-item>
                    </div>
                </div>
                <div style='width: 100%;'>
                    <van-field
                            v-if='hasOther(info.values)'
                            v-model='info.otherValue'
                            rows='2'
                            :required='1 === info.otherEnterRequired '
                            type='textarea'
                            autosize
                            label="备注"
                            placeholder='请输入其他选项的备注'
                            :maxlength='info.otherValueSize ? info.otherValueSize : 120'
                            show-word-limit
                            @change="() => {1 === info.otherEnterRequired && info.otherValue && info.otherValue.length > 0 ? info.otherErrorMessage = '' : info.otherErrorMessage = '请输入其他选项的备注'}"
                            error-message-align="right"
                            :error-message="info.otherErrorMessage"
                    />
                </div>
            </div>
            <!-- 复选框 -->
            <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.CheckBox'>
                <!-- 11111 -->
                <div class='bot-border'>
                    <van-field :label="info.label" readonly :required="info.required"></van-field>
                    <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
                    <!-- <van-checkbox-group v-model="info.checkBoxValues" @change="(value) => checkBoxChange(info)" style="width: 100%">
                        <van-checkbox v-for="(option,k) in info.options" :key="k" :name="option.id" v-model="option.select" @change="(event) => checkBoxClick(event, option, info)"
                        shape="square"  checked-color="#1890FF">
                            {{ option.attrValue }}
                        </van-checkbox>
                    </van-checkbox-group> -->
                    <!-- <van-checkbox-group v-model="info.labelDesc">
                        <van-checkbox v-for="(option,k) in info.options" :key="k" :name="option.id">{{ option.attrValue }}</van-checkbox> -->
                        <!-- <van-checkbox name="b">复选框 b</van-checkbox> -->
                    <!-- </van-checkbox-group> -->
                    <van-field :error-message="info.errorMessage" error-message-align="right">
                        <div slot="input">
                            
                        </div>
                        <template slot="input">
                            <van-checkbox-group v-model="info.checkBoxValues" @change="(value) => checkBoxChange(info)"
                                                style="width: 100%">
                                <van-row v-for="(option,k) in info.options" :key="k">
                                    <van-col span="24">
                                        <van-checkbox :name="option.id" v-model="option.select"
                                                      @click="(event) => checkBoxClick(event, option, info)"
                                                      shape="square" class="radio-item-padding option-word-wrap"
                                                      checked-color="#1890FF">{{ option.attrValue }}
                                        </van-checkbox>
                                    </van-col>
                                    <van-col span="24">
                                        <!-- 选中的项不是其他选项 -->
                                        <van-field
                                                v-show="info.itemAfterHasEnter === 1 && option.attrValue && option.attrValue !== '其他' && option.select"
                                                v-model='option.desc'
                                                @change="(value) => {setValueDesc(info.values, option, info)}"
                                                placeholder='请输入选项备注'
                                                :required='info.itemAfterHasEnterRequired === 1'
                                                style='padding: 13px 16px;'
                                                show-word-limit
                                                :maxlength='info.itemAfterHasEnterSize ? info.itemAfterHasEnterSize : 120'
                                                :error-message="option.errorMessage"
                                                error-message-align="right">
                                        </van-field>
                                    </van-col>
                                </van-row>
                            </van-checkbox-group>
                        </template>
                    </van-field>
                    <div style='width: 100%;' class="classTextarea">
                        <van-field
                                v-if='hasOther(info.values)'
                                v-model='info.otherValue'
                                rows='2'
                                :required='1 === info.otherEnterRequired '
                                type='textarea'
                                autosize
                                input-align="left"
                                placeholder='请输入其他选项的备注'
                                :maxlength='info.otherValueSize ? info.otherValueSize : 120'
                                show-word-limit
                                @change="() => {1 === info.otherEnterRequired && info.otherValue && info.otherValue.length > 0 ? info.otherErrorMessage = '' : info.otherErrorMessage = '请输入其他选项的备注'}"
                                :error-message="info.otherErrorMessage"
                                error-message-align="right"
                        />
                    </div>
                    <div v-for='(value,k) in info.values' :key="k">
                        <index-item :question='value.questions' ref='indexItem'></index-item>
                    </div>
                </div>
            </div>
            <!--多级下拉-->
            <div
                    style='background: #FFFFFF; border-bottom: 1px solid #F5F5F5;'
                    v-else-if='info.widgetType === Constants.CustomFormWidgetType.MultiLevelDropdownSelect'>
                <multi-picker
                        :columns='info.options'
                        :title='info.label'
                        :ref='info.label+Date.parse(new Date())'
                        v-model='info.values'
                        :required='info.required'
                        :error-message='info.required && info.values && info.values.length > 0 && info.values[0].attrValue && info.values[0].attrValue.length > 0 ? "" : info.errorMessage'
                        :info-value-change="() => infoValueChange(info)"
                        :placeholder='info.placeholder'
                        error-message-align="right"
                        :name='info.label'/>
                <div class="desc" v-if="info.labelDesc">{{ info.labelDesc }}</div>
            </div>
            <!--头像上传-->
            <div
                    class="bot-border"
                    v-else-if="info.widgetType === Constants.FieldExactType.Avatar">
                    <!-- 1111 -->
                <div>
                    <lano-uploader
                            :ref="info.label+Date.parse(new Date())"
                            v-model="info.values"
                            :label="info.label"
                            accept="image/*"
                            keyName="attrValue"
                            type="avatar"
                            :max="info.max"
                            :min="info.min"
                            :required="info.required"
                            :error-message='info.required && info.values && info.values.length > 0 && info.values[0].attrValue && info.values[0].attrValue.length > 0 ? "" : info.errorMessage'
                            error-message-align="right"
                    />
                </div>

            </div>
            <!--图片上传-->
            <div
                    class="bot-border"
                    v-else-if="info.widgetType === Constants.CustomFormWidgetType.SingleImageUpload || info.widgetType === Constants.CustomFormWidgetType.MultiImageUpload "
            >
                <div>
                    <lano-uploader
                            :key="new Date().getTime()"
                            :ref="info.label+Date.parse(new Date())"
                            v-model="info.values"
                            :label="info.label"
                            accept="image/*"
                            keyName="attrValue"
                            :max="info.max"
                            :min="info.min"
                            :required="info.required"
                            error-message-align="right"
                            :error-message='info.required && info.values && info.values.length > 0 && info.values[0].attrValue && info.values[0].attrValue.length > 0 ? "" : info.errorMessage'
                    />
                    <div class="desc" v-if="info.labelDesc">{{ info.labelDesc }}</div>
                </div>
            </div>
            <!--时间选择器-->
            <div class="bot-border" v-else-if="info.widgetType === Constants.CustomFormWidgetType.Time">
                <van-field
                        readonly
                        clickable
                        style="padding-top: 15px; padding-bottom: 15px;"
                        name="datetimePicker"
                        :value="info.values[0].attrValue"
                        :required="info.required"
                        :label="info.label"
                        :placeholder="info.placeholder"
                        right-icon="arrow"
                        error-message-align="right"
                        @change="() => infoValueChange(info)"
                        @click="openPicker(info)"
                        :error-message='info.errorMessage'
                />
                <van-popup v-model="info.showPicker" position="bottom">
                    <van-datetime-picker
                            type="time"
                            @confirm="(time) => onConfirm(time, info)"
                            @cancel="cancelPicker(info)"
                    />
                </van-popup>
                <div v-if="info.labelDesc" class="desc">{{ info.labelDesc }}</div>
            </div>
            <!--日期选择器-->
            <div class="bot-border"
                 v-else-if="info.widgetType === Constants.CustomFormWidgetType.Date || info.exactType === Constants.FieldExactType.Birthday">
                <van-field
                        readonly
                        clickable
                        style="padding-top: 15px; padding-bottom: 15px;"
                        name="datetimePicker"
                        :value="info.values[0].attrValue"
                        :required="info.required"
                        :label="info.label"
                        :placeholder="info.placeholder"
                        right-icon="arrow"
                        @click="openPicker(info)"
                        @change="() => infoValueChange(info)"
                        error-message-align="right"
                        :error-message='info.errorMessage'
                />
                <van-popup v-model="info.showPicker" position="bottom">
                    <van-datetime-picker
                            type="date"
                            :min-date="getMinDate(info)"
                            :max-date="getMaxDate(info)"
                            :value="getDatePickerValue(info)"
                            :columns-order="['year', 'month', 'day']"
                            :formatter="formatterDate"
                            @confirm="(time) => onDateConfirm(time, info)"
                            @cancel="cancelPicker(info)"
                    />
                </van-popup>
                <div v-if='info.labelDesc' class='desc'>{{ info.labelDesc }}</div>
            </div>
            <!--地址选择-->
            <div
                    class='bot-border'
                    v-else-if='info.widgetType === Constants.CustomFormWidgetType.Address'>
                <van-cell
                        :title='info.label'
                        is-link
                        :required='info.required'
                        :clickable='true'>
                    <selectPicker
                            type='address'
                            :columns='columns'
                            :placeholder="info.placeholder"
                            :selVal='info.values[0].attrValue'
                            v-model='info.values[0].attrValue'
                            :required='info.required'
                            :name='info.label'
                            @surecb="surecbFuntion"
                            error-message-align="right"
                            :error-message='info.required && info.values && info.values.length > 0 && info.values[0].attrValue && info.values[0].attrValue.length > 0 ? "" : info.errorMessage'
                            :ref='info.label+Date.parse(new Date())'/>
                </van-cell>
                <div v-if='info.labelDesc' class='desc'>{{ info.labelDesc }}</div>
                <div v-if='info.hasAddressDetail === 1'>
                    <van-field
                            style='padding: 13px 16px;'
                            v-model='info.value'
                            rows='2'
                            label=''
                            :required='info.hasAddressDetailRequired === 1'
                            :name='info.label'
                            type='textarea'
                            autosize
                            placeholder='请输入详细地址'
                            :maxlength='info.addressDetailSize ? info.addressDetailSize : 450'
                            :error-message='info.descErrorMessage'
                            @change="() => {info.hasAddressDetailRequired === 1 && info.value && info.value.length > 0 ? info.descErrorMessage = '' : info.descErrorMessage = '请输入详细地址' }"
                            show-word-limit
                    />
                </div>
            </div>
            <!--线与提示-->
            <attrDesc v-else-if="info.widgetType === Constants.CustomFormWidgetType.Desc" :field="info"></attrDesc>
            <splitLine v-else-if='info.widgetType === Constants.CustomFormWidgetType.SplitLine'
                       :field='info'></splitLine>
        </div>

        <!--上一页与下一页-->
        <van-col span='24' style='text-align:center; margin-top: 60px;'>
            <van-col v-if='!lastPage' span='24'>
                <van-button style="background-color:#66728C!important" round type='primary' class='attr-button btn' @click='saveCurrentFields'>{{ currentPage &&
                    currentPage.label ? currentPage.label : "下一步" }}
                </van-button>
            </van-col>
            <van-col v-if='lastPage' span='24'>
                <van-button style="background-color:#66728C!important" @click='saveCurrentFields' class='attr-button btn' type='primary'>{{myTitle?myTitle:'提交'}}</van-button>
            </van-col>
            <van-col v-if='currentPage && currentPage.canBackPrevious === 1 && currentIndex > 1' span='24'
                     style='margin-bottom: 10px; margin-top: 10px !important;'>
                <van-button @click='previousPage' class='attr-button btn' round type='default'>{{
                    currentPage.labelDesc ?
                    currentPage.labelDesc : '上一页'
                    }}
                </van-button>
            </van-col>
        </van-col>

    </van-form>
</template>

<script>

    // import CustomFormService from '@/api/customForm';
    import {parseTime} from "@/components/utils/index";
    import {ImagePreview,Row,Col,Cell,Form,Button,Field,DropdownMenu, DropdownItem } from "vant";
    import {Checkbox, CheckboxGroup,RadioGroup, Radio,DatetimePicker,Popup } from "vant";
    import selectPicker from '@/components/select/index'
    import lanoUploader from '@/components/upload/index'
    import multiPicker from '@/components/multiPicker/index'
    import attrDesc from '@/components/arrt/descindex'
    import splitLine from '@/components/arrt/splitLineindex'
    import indexItem from '@/components/arrt/indexItem'
    import address from '@/pubilc/address.js'
   
    export default {
        components:{
            [Row.name]:Row,
            [Col.name]:Col,
            [Cell.name]:Cell,
            [Form.name]:Form,
            [Button.name]:Button,
            [Field.name]:Field,
            [DropdownMenu.name]:DropdownMenu,
            [DropdownItem.name]:DropdownItem,
            [Checkbox.name]:Checkbox,
            [CheckboxGroup.name]:CheckboxGroup,
            [RadioGroup.name]:RadioGroup,
            [Radio.name]:Radio,
            [DatetimePicker.name]:DatetimePicker,
            [Popup.name]:Popup,
            Checkbox,
            CheckboxGroup,
            selectPicker,
            lanoUploader,
            multiPicker,
            attrDesc,
            splitLine,
            indexItem
        },
        name: 'attr-page',
        props: {
            myTitle: {
                type: String
            },
            baseInfo: {
                type: Array
            },
            allFields: {
                type: Array
            },
            submit: {
                type: Function
            }
        },
        mounted(){
            // console.log(this.allFields)
            // console.log(this.Constants)
                this.initFields();
                this.$forceUpdate();
        },
        data() {
            return {
                headerConfig: {
                    title: '健康档案'
                },
                totalPage: 1, // 总页数
                currentIndex: 1, // 当前下标
                lastPage: false, // 是否最后一页
                currentFields: [], // 当前展示的元素
                fieldsMap: {}, // 根据分页组件 对元素表单进行分组
                pageField: [], // 分页组件
                pageNo: 0,
                currentPage: undefined, // 当前分页
                columns: [],
                attrInfo: [],
                Birthday: '',
                sex: undefined,
                user: '',
                // user: this.$store.getters.user,
                isShow: false,
                checkValus: [],
                type: '',
                infoIndex: -1,
                maxDate: new Date(), // 生日要求最大值不能大于当前时间,
                thistitle:'请选择'
            };
        },
        methods: {
            // changeDevelop(i,k){
            //     let that = this
            //     k.forEach(element => {
            //         if(element.value === i){
            //             that.thistitle = element.text
            //         }
                    
            //     });
            // },
            surecbFuntion(i){
                // console.log(i)
            },
            // 手动校验currentFields
            validate(currentFields) {
                if (currentFields.length === 0) {
                    return true;
                }
                let validateResult = true;
                for (let i = 0; i < currentFields.length; i++) {
                    if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Date
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Time
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.FullName
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.SingleLineText
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.MultiLineText
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.SingleImageUpload
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.MultiImageUpload
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Avatar
                    ) {
                        if (currentFields[i].required) {
                            // 校验是否已经有值
                            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
                            } else {
                                validateResult = false;
                                // 校验不通过。设置提醒文字.
                                if (currentFields[i].placeholder) {
                                    const message = currentFields[i].placeholder;
                                    currentFields[i].errorMessage = message;
                                } else {
                                    if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Date || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Time) {
                                        currentFields[i].errorMessage = '请选择时间';
                                    } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.FullName) {
                                        currentFields[i].errorMessage = '请输入姓名';
                                    } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.SingleImageUpload
                                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.MultiImageUpload) {
                                        currentFields[i].errorMessage = '请上传图片';
                                    } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Avatar) {
                                        currentFields[i].errorMessage = '请上传头像';
                                    } else {
                                        currentFields[i].errorMessage = '请填写' + currentFields[i].label;
                                    }
                                }
                            }
                        }
                    } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Address) {
                        if (currentFields[i].required) {
                            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
                            } else {
                                validateResult = false;
                                if (currentFields[i].placeholder) {
                                    const message = currentFields[i].placeholder;
                                    currentFields[i].errorMessage = message;
                                } else {
                                    currentFields[i].errorMessage = '请选择行政区划';
                                }
                            }
                        }
                        if (currentFields[i].hasAddressDetail === 1 && currentFields[i].hasAddressDetailRequired === 1) {
                            if (currentFields[i].value && currentFields[i].value.length > 0) {
                            } else {
                                validateResult = false;
                                currentFields[i].descErrorMessage = '请输入详细地址';
                            }
                        }
                    } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.MultiLevelDropdownSelect) {
                        if (currentFields[i].required) {
                            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
                            } else {
                                validateResult = false;
                                if (currentFields[i].placeholder) {
                                    const message = currentFields[i].placeholder;
                                    currentFields[i].errorMessage = message;
                                } else {
                                    currentFields[i].errorMessage = '请选择选项';
                                }
                            }
                        }
                    } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.CheckBox
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Radio
                        || currentFields[i].widgetType === this.Constants.CustomFormWidgetType.DropdownSelect) {
                        if (currentFields[i].required) {
                            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
                                // 判断当前输入的选项是否为其他选项
                                for (let j = 0; j < currentFields[i].values.length; j++) {
                                    if (currentFields[i].values.questions && currentFields[i].values.questions.length > 0) {
                                        const result = this.validate(currentFields[i].values.questions);
                                        if (!result) {
                                            validateResult = false;
                                        }
                                    }
                                    // 其他选择输入框是否必填
                                    if (currentFields[i].hasOtherOption === 1 && currentFields[i].otherEnterRequired === 1) {
                                        if (this.hasOther(currentFields[i].values)) {
                                            if (!currentFields[i].otherValue) {
                                                validateResult = false;
                                                currentFields[i].otherErrorMessage = '请输入其他选项的备注';
                                            }
                                        }
                                    }
                                }
                                // 要求输入框必填
                                if (currentFields[i].itemAfterHasEnter === 1 && currentFields[i].itemAfterHasEnterRequired === 1) {
                                    const valuesString = JSON.stringify(currentFields[i].values);
                                    for (let j = 0; j < currentFields[i].options.length; j++) {
                                        if (valuesString.findIndex(currentFields[i].options[j].id) > -1) {
                                            if (currentFields[i].options[j].desc && currentFields[i].options[j].desc.length > 0) {
                                            } else {
                                                validateResult = false;
                                                currentFields[i].options[j].errorMessage = '请填写选项备注';
                                            }
                                        }
                                    }
                                }
                                // 判断是否开启了详情输入框
                            } else {
                                validateResult = false;
                                if (currentFields[i].placeholder) {
                                    const message = currentFields[i].placeholder;
                                    currentFields[i].errorMessage = message;
                                } else {
                                    currentFields[i].errorMessage = '请选择选项';
                                }
                            }
                        }
                    } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Number) {
                        if (currentFields[i].required) {
                            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
                            } else {
                                validateResult = false;
                                if (currentFields[i].placeholder) {
                                    const message = currentFields[i].placeholder;
                                    currentFields[i].errorMessage = message;
                                } else {
                                    currentFields[i].errorMessage = '请填写数值';
                                }
                            }
                        }
                        // 校验数值是否符合标准
                        if (currentFields[i].values && currentFields[i].values.length > 0 || currentFields[i].values[0].attrValue) {

                        }
                    }
                }
                return validateResult;
            },
            checkBoxChange(info) {
                if (info.required && info.checkBoxValues && info.checkBoxValues.length === 0) {
                    info.errorMessage = info.placeholder;
                } else {
                    info.errorMessage = '';
                }
            },
            infoValueChange(info) {
                if (!info.required) {
                    info.errorMessage = '';
                    return;
                }
                if(typeof info.values !== 'object'){
                    info.values = eval("(" + info.values + ")")
                }
                if (info.values && info.values[0] && info.values[0].attrValue && info.values[0].attrValue.length > 0) {
                    info.errorMessage = '';
                } else {
                    if (info.placeholder) {
                        info.errorMessage = info.placeholder;
                    }
                }
            },
            saveCurrentFields() {
                const validateStatue = this.validate(this.currentFields);
                // console.log(this.lastPage);
                if (!this.lastPage) {
                    //  console.log(validateStatue);
                    if (!validateStatue) {
                        this.$forceUpdate();
                        this.$toast.fail('您有表单项未完善，请检查完善后进行下一步');
                        return;
                    }
                    const thisObj = {
                        content: this.currentFields,
                        MarkTip:'has'
                    }
                    this.submit(thisObj);
                } else {
                    if (!validateStatue) {
                        this.$forceUpdate();
                        this.$toast.fail('您有表单项未完善，请检查完善后提交');
                        return;
                    }
                    const thisObj = {
                        content: this.currentFields,
                        MarkTip:'end'
                    }
                    this.submit(thisObj);
                }
            },
            toggle(index) {
                this.$refs.checkboxes[index].toggle();
            },
            checkBoxClick(event, option, info) {
                // console.log(option)
                if (!option.select) {
                    option.select = true;
                    const val = {valueText: option.attrValue, attrValue: option.id, questions: option.questions};
                    // console.log(info.values)
                    if (info.values && info.values.length === 1) {
                        if (!info.values[0].attrValue || info.values[0].attrValue.length === 0) {
                            info.values = [];
                        }
                    }
                    info.values.push(val);
                } else {
                    option.select = false;
                    for (let i = 0; i < info.values.length; i++) {
                        if (info.values[i].attrValue === option.id) {
                            info.values.splice(i, 1);
                            break;
                        }
                    }
                }
                this.$forceUpdate();
            },
            // 设置日期选项器的打开的最小时间
            getMinDate(info) {
                if (info.defineChooseDate === 1 && info.defineChooseDateType !== 1) {
                    return new Date();
                } else {
                    let year = new Date().getFullYear();
                    year = year - 100;
                    return new Date(year + '/' + '01/' + '01');
                }
            },
            // 设置日期选择器的打开的最大时间
            getMaxDate(info) {
                if (info.defineChooseDate === 1 && info.defineChooseDateType !== 2) {
                    return new Date();
                } else {
                    // 获取一个 100年后的时间
                    let year = new Date().getFullYear();
                    year = year + 100;
                    return new Date(year + '/' + '12/' + '31');
                }
            },
            // 设置日期选择器打开时选择的的时间
            getDatePickerValue(info) {
                if (info.values && info.values[0] && info.values[0].attrValue) {
                    const dateStr = info.values[0].attrValue.replace(/-/g, '/');
                    return new Date(dateStr);
                } else {
                    if (info.defaultValue) {
                        return new Date(info.defaultValue);
                    }
                }
            },
            formatterDate(type, val) {
                if (type === 'year') {
                    return val;
                }
                if (type === 'month') {
                    return val;
                }
                if (type === 'day') {
                    return val;
                }
                return val;
            },
            // 关闭 时间选择器
            cancelPicker(info) {
                info.showPicker = false;
                this.$forceUpdate();
            },
            // 打开时间选择器
            openPicker(info) {
                info.showPicker = true;
                this.$forceUpdate();
            },
            onDateConfirm(date, info) {
                const val = parseTime(date, "{y}-{m}-{d}");
                info.values[0].valueText = val;
                info.values[0].attrValue = val;
                info.showPicker = false;
                this.$forceUpdate();
            },
            // 确定选择时间
            onConfirm(time, info) {
                info.values[0].valueText = time;
                info.values[0].attrValue = time;
                info.showPicker = false;
                this.$forceUpdate();
            },
            radioClick(id, info) {
                // console.log(info)
                let option = undefined;
                for (let i = 0; i < info.options.length; i++) {
                    if (id === info.options[i].id) {
                        info.options[i].desc = '';
                        option = info.options[i];
                    }
                }
                info.values[0].valueText = option.attrValue;
                info.values[0].questions = option.questions;
                info.errorMessage = '';
                this.$forceUpdate();
            },
            // 设置 option 对应的value的desc
            setValueDesc(values, option, info) {
                if (info.itemAfterHasEnter === 1 && info.itemAfterHasEnterRequired === 1) {
                    if (option.desc && option.desc.length > 0) {
                        option.errorMessage = '';
                    } else {
                        option.errorMessage = '请填写选项备注';
                    }
                }
                if (values && values.length >= 1) {
                    for (let i = 0; i < values.length; i++) {
                        if (values[i].attrValue === option.id) {
                            values[i].desc = option.desc;
                        }
                    }
                }
            },
            /**
             * 下拉框
             */
            itemChage(values, options, info) {
                options.forEach(ele => {
                    if (null != values[0].attrValue && values[0].attrValue === ele.id) {
                        values[0].valueText = ele.attrValue;
                        values[0].attrValue = ele.id;
                        values[0].questions = ele.questions;
                    }
                });
                if (values[0] && values[0].attrValue) {
                    info.errorMessage = '';
                }
            },
            /**
             * 获取省市区三级联动数据
             * */
            getAddress() {
                // CustomFormService.getAddress()
                //     .then(result => {
                //         if (result.success) {
                            this.columns = address;
                //         }
                //     })
                //     .catch(err => {
                //     });
            },
            hasOther(values) {
                let has = false;
                if (values && values.length > 0) {
                    for (let i = 0; i < values.length; i++) {
                        if (values[i].valueText === '其他') {
                            has = true;
                            break;
                        }
                    }
                }
                return has;
            },
            /**
             * 计算年龄
             * @param strBirthday
             * @returns {number}
             */
            jsGetAge(strBirthday) {
                // 传入形式yyyy-MM-dd
                // strBirthday = util.formatTime(strBirthday);转换成yyyy-MM-dd形式
                var returnAge;
                var strBirthdayArr = strBirthday.split('-');
                var birthYear = strBirthdayArr[0];
                var birthMonth = strBirthdayArr[1];
                var birthDay = strBirthdayArr[2];
                var d = new Date();
                var nowYear = d.getFullYear();
                var nowMonth = d.getMonth() + 1;
                var nowDay = d.getDate();
                if (nowYear == birthYear) {
                    returnAge = 0; // 同年 则为0岁
                } else {
                    var ageDiff = nowYear - birthYear; // 年之差
                    if (ageDiff > 0) {
                        if (nowMonth == birthMonth) {
                            var dayDiff = nowDay - birthDay; // 日之差
                            if (dayDiff < 0) {
                                returnAge = ageDiff - 1;
                            } else {
                                returnAge = ageDiff;
                            }
                        } else {
                            var monthDiff = nowMonth - birthMonth; // 月之差
                            if (monthDiff < 0) {
                                returnAge = ageDiff - 1;
                            } else {
                                returnAge = ageDiff;
                            }
                        }
                    } else {
                        returnAge = -1; // 返回-1 表示出生日期输入错误 晚于今天
                    }
                }
                return returnAge; // 返回周岁年龄
            },
            cb(value, sourceObj) {
                if (value && sourceObj) {
                    sourceObj.attrValue = value.id;
                    sourceObj.valueText = value.attrValue;
                }
            },
            setValue(label, options) {
                label = options[0].attrValue;
                return label;
            },
            resolveOptions(options) {
                if (!options) {
                    return [];
                }
                options.forEach(ele => {
                    ele.text = ele.attrValue;
                    ele.value = ele.id;
                    ele.key = ele.key;
                });
                return options;
            },
            onRead(type, ind) {
                const that = this
                return files => {
                    
                    if (files) {
                        var formData = new FormData()
                        formData.append('file', files.file)
                        formData.append('folderId', 1308295372556206080)

                        if (this.type === 'img') {
                            const isJPG = (files.file.type === 'image/jpeg' || files.file.type === 'image/png')
                            if (!isJPG) {
                            this.$message.error('上传图片只能为jpg或者png!')
                            return
                            }
                        }
                        const isLt2M = files.file.size / 1024 / 1024 < 2
                        if (!isLt2M) {
                            this.$message.error('上传头像图片大小不能超过 2MB!')
                            return
                        }
                        Api.updateImg(formData).then((res) => {
                            if (res.data.code === 0) {
                                this.$toast.success('上传成功');
                                    that.$set(
                                        that['currentFields'][ind]['values'][0],
                                        'attrValue',
                                        res.data.data.url
                                    );
                            } else {
                                this.$message({
                                    message: '文件上传失败！',
                                    type: 'error'
                                })
                            }
                        })
                    }
                };
            },

            // 回填原有的屬性
            resloveValues(ind) {
                let that = this
                return selOptions => {
                    try {
                        let temp = JSON.parse(
                            JSON.stringify(this['currentFields'][ind]['values'])
                        );
                        let isNotExist = [];
                        for (let j = 0, i = temp.length - 1; i >= j; i--) {
                            let hasExist = this.isExistValue(temp[i], selOptions);
                            if (hasExist < 0) {
                                temp.splice(i, 1);
                            }
                        }
                        if (selOptions.length > 0) {
                            selOptions.forEach(ele => {
                                ele.attrValue = ele.id;
                                delete ele.id;
                            });
                            temp = temp.concat(selOptions);
                        }
                        // console.log(temp)
                        that.$set(that['currentFields'][ind], 'values', temp);
                    } catch (err) {
                        // console.log(err);
                    }
                };
            },
            isExistValue(value, selOptions) {
                let isExistInd = -1;
                selOptions.forEach((attrEle, ind) => {
                    if (value.attrValue && value.attrValue == attrEle.id) {
                        isExistInd = ind;
                    }
                });
                if (isExistInd > -1) {
                    selOptions.splice(isExistInd, 1);
                }
                return isExistInd;
            },
            // 设置options的初始选中状态
            initCheckBox(info) {
                let attrInfo = info;
                let temp = info.values;
                if (temp) {
                    temp.forEach(ele => {
                        attrInfo.options.forEach(option => {
                            if (ele) {
                                if (ele.attrValue == option.id) {
                                    option.select = true;
                                }
                            }
                        });
                    });
                }
                return attrInfo.options;
            },
            initFields() {
                this.currentFields = [];
                // console.log(this.allFields)
                if (this.allFields && this.allFields.length > 0) {
                    let tempArray = [];
                    const that = this
                    for (let i = 0; i < that.allFields.length; i++) {
                        if(!that.allFields[i].checkBoxValues){
                            that.allFields[i].checkBoxValues = []
                        }else if(typeof that.allFields[i].checkBoxValues !== 'object'){
                            that.allFields[i].checkBoxValues = eval("(" + that.allFields[i].checkBoxValues + ")")
                        }
                        
                        if(typeof that.allFields[i].values !== 'object'){
                            that.allFields[i].values = eval("(" + that.allFields[i].values + ")")
                        }
                        // console.log(that.allFields[i])
                        if (that.allFields[i].widgetType === that.Constants.CustomFormWidgetType.Page) {
                            
                            that.pageField.push(that.allFields[i]);
                            that.fieldsMap[that.totalPage] = tempArray;
                            that.totalPage++;
                            tempArray = [];
                        } else {
                            tempArray.push(that.allFields[i]);
                        }
                        
                    }
                    if (tempArray.length > 0) {
                        that.fieldsMap[that.totalPage] = tempArray;
                    }
                    // console.log(that.fieldsMap)
                }
                if (this.totalPage > 1) {
                    this.currentPage = this.pageField[this.pageNo];
                    this.lastPage = false;
                } else {
                    this.lastPage = true;
                }
                this.currentFields = this.fieldsMap[this.currentIndex];
                this.$forceUpdate();
            },
            nextPage() {
                this.pageNo++;
                this.currentIndex++;
                if (this.pageNo === this.pageField.length) {
                    this.lastPage = true;
                } else {
                    this.currentPage = this.pageField[this.pageNo];
                }
                // console.log(this.currentPage);
                // console.log(this.fieldsMap[this.currentIndex]);
                this.currentFields = this.fieldsMap[this.currentIndex];
            },
            previousPage() {
                this.pageNo--;
                this.currentIndex--;
                this.lastPage = false;
                this.currentPage = this.pageField[this.pageNo];
                this.currentFields = this.fieldsMap[this.currentIndex];
            }
        },
        created() {
            this.getAddress();
            if (this.baseInfo && this.baseInfo.length > 0) {
                for (let i = 0; i < this.baseInfo.length; i++) {
                    
                    if (this.baseInfo[i].widgetType === this.Constants.CustomFormWidgetType.Date &&
                        this.baseInfo[i].exactType === this.Constants.FieldExactType.Birthday) {
                            
                        if (this.baseInfo[i].values && this.baseInfo[i].values[0]) {
                            this.Birthday = this.baseInfo[i].values[0].attrValue;
                        }
                    }
                    if (this.baseInfo[i].widgetType === this.Constants.CustomFormWidgetType.Radio &&
                        this.baseInfo[i].exactType === this.Constants.FieldExactType.Gender) {
                        if (this.baseInfo[i].values && this.baseInfo[i].values[0]) {
                            if (this.baseInfo[i].values[0].valueText === '男') {
                                this.sex = 0;
                            } else if (this.baseInfo[i].values[0].valueText === '女') {
                                this.sex = 1;
                            }
                        }
                    }
                }
            }
        },
        watch: {
            allFields(newValue, oldValue) {
                this.initFields();
                this.$forceUpdate();
            }
        },
        
        computed: {
            
            bmi(index) {
                const that = this
                return index => {
                    let height = undefined,
                        weight = undefined;
                    let bmiIndex = -1;
                    this.currentFields.forEach((ele, i) => {
                        if (
                            ele.exactType === this.Constants.FieldExactType.Weight
                        ) {
                            weight = ele.values[0] && ele.values[0].attrValue;
                        } else if (
                            ele.exactType === this.Constants.FieldExactType.Height
                        ) {
                            height = ele.values[0] && ele.values[0].attrValue / 100;
                        }
                        if (ele.exactType === this.Constants.FieldExactType.BMI) {
                            bmiIndex = i;
                        }
                    });
                    if (weight !== undefined && weight > 0 && height !== undefined && height > 0) {
                        let bmi = weight / Math.pow(height, 2);
                        let bmiValue = bmi ? (bmi === Infinity ? 0 : bmi.toFixed(2)) : 0;
                        if (bmiIndex > -1) {
                            that.$set(
                                that['currentFields'][bmiIndex]['values'][0],
                                'attrValue',
                                bmiValue
                            );
                        }
                        return bmiValue;
                    } else {
                        return '-';
                    }
                };
            },
            gfr(index) {
                let that = this
                return index => {
                    var scr = undefined,
                        age = this.user.age,
                        sex = this.user.sex;
                    if (!age || age === 0) {
                        age = this.age;
                    }
                    if (!sex) {
                        sex = this.sex;
                    }
                    this.allFields.forEach((ele, i) => {
                        if (
                            ele.exactType === this.Constants.FieldExactType.SCR
                        ) {
                            scr = ele.values[0] && ele.values[0].attrValue;
                        }
                    });
                    if (!scr || scr === 0) {
                        return '-';
                    }
                    let temNum =
                        186 * Math.pow(scr / 88.4, -1.154) * Math.pow(1, -0.203);
                    if (sex === 0) {
                        temNum = temNum * 0.742;
                    }
                    let num = temNum
                        ? temNum === Infinity
                            ? 0
                            : temNum.toFixed(2)
                        : 0;
                    if (index > -1) {
                        that.$set(
                            that['currentFields'][index]['values'][0],
                            'attrValue',
                            num
                        );
                    }
                    return num;
                };
            },
            ccr(index) {
                const that = this
                return index => {
                    let sex = this.user.sex;
                    let age = this.user.age;
                    let weight = this.user.weight;
                    if (!age || age === 0) {
                        age = this.age;
                    }
                    if (!sex) {
                        sex = this.sex;
                    }
                    let scr = undefined;
                    this.currentFields.forEach((ele, i) => {
                        if (!this.weight || this.weight === 0 || this.weight === '0') {
                            if (
                                ele.exactType === this.Constants.FieldExactType.Weight
                            ) {
                                weight = ele.values[0] && ele.values[0].attrValue;
                            }
                        }
                        if (
                            ele.exactType === this.Constants.FieldExactType.SCR
                        ) {
                            scr = ele.values[0] && ele.values[0].attrValue;
                        }
                    });
                    if (!scr || scr === 0) {
                        return '-';
                    }
                    let temNum = ((140 - age) * weight) / (0.818 * scr);
                    if (sex === 0) {
                        temNum = temNum * 0.85;
                    }
                    const num = temNum
                        ? temNum == Infinity
                            ? 0
                            : temNum.toFixed(2)
                        : 0;
                    if (index > -1) {
                        that.$set(
                            that['currentFields'][index]['values'][0],
                            'attrValue',
                            num
                        );
                    }
                    return num;
                };
            },
            ckd: function (index) {
                let gfr = this.gfr(index);
                const that = this
                return index => {
                    let ckd = '';
                    if (this.gfr(index) >= 90) {
                        ckd = 'CKD 1期';
                    } else if (gfr >= 60) {
                        ckd = 'CKD 2期';
                    } else if (gfr >= 30) {
                        ckd = 'CKD 3期';
                    } else if (gfr >= 15) {
                        ckd = 'CKD 4期';
                    } else if (gfr < 15) {
                        ckd = 'CKD 5期';
                    }
                    if (gfr === 0) {
                        ckd = '-';
                    }
                    that.$set(that['currentFields'][index]['values'][0], 'attrValue', ckd);
                    return ckd;
                };
            },
            // 从 组件中获取年龄
            age: function () {
                if (this.baseInfo && this.baseInfo.length > 0) {
                    let age = 0;
                    let Birthday = this.Birthday;
                    if (Birthday || Birthday.length === 0) {
                        for (let i = 0; i < this.baseInfo.length; i++) {
                            const ele = this.baseInfo[i];
                            if (
                                ele.widgetType ===
                                this.Constants.CustomFormWidgetType.Date &&
                                ele.exactType === this.Constants.FieldExactType.Birthday
                            ) {
                                Birthday = ele.values[0].attrValue;
                                break;
                            }
                        }
                    }
                    if (Birthday) {
                        const split = this.Birthday.split('-');
                        if (split.length > 0) {
                            const date = new Date();
                            const now = date.getFullYear();
                            age = now - parseInt(split[0]);
                        }
                    }
                    return age;
                } else {
                    return 0;
                }
            }
        }

    };
</script>
<style lang='less' scoped>

    .single {
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 1;
        overflow: hidden;
        display: flex;
        position: absolute;
        padding-left: 16px;
        height: 100%;
        width: 28%;
        font-size: 14px;
        padding-top: 15px;
        color: #333333;
    }

    /deep/.van-cell-width50 {
        width: 45%;
        height: auto;
    }

    /deep/ .option-word-wrap {
        word-wrap: break-word;
        word-break: normal;
        text-align: left;

        .van-checkbox__label {
            width: 100%;
        }
    }

    /deep/ .attr-radio-group {
        background-color: #fff;
        width: 100%;
        padding: 10px 16px 10px 16px;
    }

    /deep/ .radio-item-padding {
        padding-top: 5px;
        padding-bottom: 5px;
    }

    /deep/ .van-field__label {
        color: #333333;
        font-size: 14px;
    }

    /deep/ .van-cell {
        color: #333333;
        font-size: 14px;
    }

    .desc {
        color: #999999;
        font-size: 12px;
        background: #FFFFFF;
        padding: 0px 15px 10px 15px;
    }

    /deep/ .van-dropdown-menu {
        display: flex;
        font-size: 14px
    }

    /deep/ .van-dropdown-menu__bar {
        width: 100%;
        background: #FFFFFF;
        height:22px;
        box-shadow: 0 0px rgba(100, 101, 102, .08);
    }

    /deep/ .van-dropdown-menu__item {
        justify-content: flex-end!important;
        padding-right: 20px;
        display: flex;
    }

    /deep/ .van-dropdown-menu van-hairline--top-bottom {
        border-width: 0px 0;
    }

    /deep/ .van-dropdown-menu__title {
        font-size: 14px;
        max-width: 60%;
    }


    /deep/ .van-field__control {
        text-align: right;
        color: #0e0e0e;
    }

    .bot-border {
        position: relative;
        border-bottom: 1px solid #f5f5f5;

        &:after {
            position: absolute;
            box-sizing: border-box;
            content: " ";
            pointer-events: none;
            top: -50%;
            right: -50%;
            bottom: -50%;
            left: -50%;
            // border-bottom: 1px solid #ebedf0;
            -webkit-transform: scale(0.5);
            transform: scale(0.5);
        }
    }

    .attr-button {
        width: 60% !important;
        border-radius: 6px;
        margin-bottom: 20px;
        height: 48px !important;
        border-radius: 24px;
    }

    .entry-group-baseinfo {
        /deep/ .van-cell--clickable {
            background-color: #ffffff;
        }
    }

    /deep/ .van-cell::after {
        border-bottom: 0px solid #ebedf0;
    }

</style>
