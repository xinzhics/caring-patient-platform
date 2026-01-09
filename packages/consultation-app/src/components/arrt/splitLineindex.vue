<template>
    <van-col span="24" class="attr-split-line">
        <div style="margin-top: 10px; margin-bottom: 10px;" :class="[field.dividerStyle === 'hairline' ? 'link-solid' : 'link-dashed', field.dividerWidth === 100 ? 'link-kuan100' : 'link-kuan80']"></div>
    </van-col>
</template>
<script>
    export default {
        props: {
            field: {
                type: Object
            }
        },
        data: function() {
            return {
                // field: {
                //     dividerStyle: "hairline",
                //     dividerWidth: 100
                // }
            };
        }
    };
</script>
<style lang="less" scoped>
    .attr-split-line {
        .link-solid {
            height: 0.5px;
            border-top: solid #DCDFE6 0.5px;
        }
        .link-kuan100 {
            width: 100%;
        }
        .link-kuan80 {
            margin-left: 20px;
            margin-right: 20px;
        }
        .link-dashed {
            height: 0.5px;
            border-top: dashed #f5f5f5 0.5px;
        }
    }
</style>
