/*
 异步 ajax
 1.从后台获取店铺分类 区域信息 将他们填充到前台的下拉列表、控件
 2.将表单的信息，然后转发到后台 去注册店铺
 */

$(function () {//开始加载js文件
    var shopId=getQueryString("shopId");
    var isEdit=shopId?true:false;
    var initUrl = '/o2o/shopadmin/getshopinitinfo';  //初始化url,获取店铺的初始信息
    var registerShopUrl = '/o2o/shopadmin/registershop';//注册店铺
    var shopInfoUrl="/o2o/shopadmin/getshopbyid?shopId="+shopId;
    var editShopUrl='/o2o/shopadmin/modifyshop';
    //调用方法
    if(!isEdit){
        getShopInitInfo();
    }else {
        getShopInfo(shopId);
    }

    function getShopInfo(shopId) {
        $.getJSON(shopInfoUrl,function (data) {
            if (data.success){
                var shop=data.shop;
                $('#shop-name').val(shop.shopName);
                $('#shop-addr').val(shop.shopAddr);
                $('#shop-phone').val(shop.phone);
                $('#shop-desc').val(shop.shopDesc);
                var shopCategory='<option data-id="'
                      +shop.shopCategory.shopCategoryId+'"selected>'
                      +shop.shopCategory.shopCategoryName+'</option>';
                var tempAreaHtml='';
                data.areaList.map(function (item, index) {
                    tempAreaHtml+='<option data-id="'+item.areaId+'">'
                        +item.areaName+'</option>';
                });
                $('#shop-category').html(shopCategory);
                $('#shop-category').attr('disabled','disabled');
                $('#area').html(tempAreaHtml);
                $("#area option[data-id='"+shop.area.areaId+"']").attr("selected","selected");
            }
        });
    }

    //获取店铺的基本信息
    //这个方法，主要是后台获取到区域的信息，以及店铺分类的信息，填充到两个下拉列表控件中去
    function getShopInitInfo() {
        $.getJSON(initUrl, function (data) {
            if (data.success) {
                var tempHtml = '';
                var tempAreaHtml = '';
                data.shopCategoryList.map(function (item, index) {
                    tempHtml += '<option data-id="' + item.shopCategoryId
                        + '">' + item.shopCategoryName + '</option>';
                });
                data.areaList.map(function (item, index) {
                    tempAreaHtml += '<option data-id="' + item.areaId + '">'
                        + item.areaName + '</option>';
                });
                $('#shop-category').html(tempHtml);
                $('#area').html(tempAreaHtml);
            }
        });
    }

    //这个方法，点击提交，获取表单的内容
    $('#submit').click(function () {
        var shop = {};
        if (isEdit) {
            shop.shopId = shopId;
        }
        shop.shopName = $('#shop-name').val();
        shop.shopAddr = $('#shop-addr').val();
        shop.phone = $('#shop-phone').val();
        shop.shopDesc = $('#shop-desc').val();
        shop.shopCategory = {
            shopCategoryId: $('#shop-category').find('option').not(function () {
                return !this.selected;
            }).data('id')
        };
        shop.area = {
            areaId: $('#area').find('option').not(function () {
                return !this.selected;
            }).data('id')
        };
        var shopImg = $('#shop-img')[0].files[0];
        var formData = new FormData();
        formData.append('shopImg', shopImg);
        formData.append('shopStr', JSON.stringify(shop));

        //传进验证码
        var verifyCodeActual = $('#j_captcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码！');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);

        //提交到后台
        $.ajax({
            url: (isEdit ? editShopUrl : registerShopUrl),
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            cache: false,
            success: function (data) {
                if (data.success) {
                    $.toast('提交成功!'); //盘住
                } else {
                    $.toast('提交失败!' + data.errMsg); //盘住
                }
                //    不管提交成功与否，都去更新一次验证码
                $('#captcha_img').click();
            }
        });
    });
});