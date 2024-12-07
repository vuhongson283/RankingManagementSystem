{
    var edited = false;
    //nếu chưa có bất kì thay đổi nào -> disable button Cancel, Save
    $(document).ready(function () {
        $('#button-cancel').prop("disabled", true);
        $('#button-cancel')[0].classList.add('disabled');
        $('#button-save').prop("disabled", true);
        $('#button-save')[0].classList.add('disabled');

        $('.button-remove').click(function () {
            $('#button-cancel').prop("disabled", false);
            $('#button-save').prop("disabled", false);
            edited = true;
        });

        $('.weight, .option-select').change(function () {
            $('#button-cancel').prop("disabled", false);
            $('#button-save').prop("disabled", false);
            edited = true;
        });

        $('#button-add-new').click(function () {
            $('#button-cancel').prop("disabled", false);
            $('#button-save').prop("disabled", false);
            edited = true;
        });

        //input name for new rank title, Nếu input rỗng => ẩn nut Add
        if ($('#input-new-title').val() == '') {
            $('#button-add-new').hide();
        } else {
            $('#button-add-new').show();
        }
    });

    $(document).ready(function () {
        calculateOrder();
        calculateRankScore();
        addEventForSelectOption();
        addEventForRemoveBtn();
    });

    //mảng lưu trữ những row đã xóa tạm thời(chưa cập nhật vào db cho đến khi save)
    var markedToRemove = [];

    //them rank title
    $('#button-add-new').click(function () {
        let rankTitleName = removeExtraSpaces($('#input-new-title').val());
        let rankTitleNameArr = $('.rank-title-row .rank-title-name').map(function () {
            return $(this).text();
        }).get();
        if (rankTitleNameArr.includes(rankTitleName)) {
            $('#notificationErrorModal .modal-body h5').text("Rank Title name is duplicated.");
            $('#notificationErrorModal').modal('show');
        } else {
            $.ajax({
                url: '/api/ranking-decision-management-api/rank-title-config/' + $('#rankingDecisionId').val(),
                type: 'GET',
                data: {
                    'rankTitleName': rankTitleName
                },
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8', // Set contentType phù hợp
                processData: true, // jQuery sẽ tự động serialize dữ liệu
                success: function (data, textStatus, jqXHR) {
                    console.log(data);
                    let optionSetHtml = " <tr class=\"rank-title-row\">\n" +
                        "                        <td></td>\n" +
                        "                        <td class=\"rank-title-name\">" + rankTitleName + "</td>\n" +
                        "                        <td class=\"rank-score\" id=\"" + rankTitleName + "\"></td>\n";
                    if (jqXHR.status == 200) { //tạm thời thêm rank title mới vào bảng
                        $.each(data, function (index, item) {
                            let cols = " <td>\n" +
                                "                            <select name=\"\" id=\"" + rankTitleName + "\" class=\"option-select\">\n" +
                                "                                <option value=\"\" data-optionScore=\"\" selected></option> "
                            let options = item.rankingDecisionCriteriaId.criteria.options;
                            $.each(options, function (optionIndex, option) {
                                cols += "<option" +
                                    "                    value=\"" + option.id + "\"\n" +
                                    "                    data-optionScore=\"" + option.score + "\"\n" +
                                    "                    data-weight=\""+item.weight+"\"\n" +
                                    "                    data-maxScore=\""+item.rankingDecisionCriteriaId.criteria.options[0].score+"\"\n" +
                                    "                    >" + option.name + "</option>";
                            });
                            cols += "           </select>\n" +
                                "          </td> ";
                            optionSetHtml += cols;
                        });
                        optionSetHtml += "      <td>\n" +
                            "                            <div class=\"button-remove\"\n" +
                            "                                    id=\"" + rankTitleName + "\">\n" +
                            "                                <i class=\"fa fa-trash\" aria-hidden=\"true\"></i>\n" +
                            "                            </div>\n" +
                            "                        </td>\n" +
                            "                    </tr> "

                        console.log(optionSetHtml);
                        $('tbody').append(optionSetHtml);
                        calculateOrder();
                        addEventForRemoveBtn();
                        calculateRankScore();
                        addEventForSelectOption();
                        if(markedToRemove.includes(rankTitleName)){
                            markedToRemove = markedToRemove.filter(function(item) {
                                return item != rankTitleName;
                            });
                        }

                    }
                    if (jqXHR.status == 202) { //ko thêm mới ví đã trùng tên
                        $('#notificationErrorModal .modal-body h5').text(jqXHR.responseText);
                        $('#notificationErrorModal').modal('show');
                    }
                    $('#input-new-title').val("");
                    $('#button-add-new').hide();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $('#notificationErrorModal .modal-body h5').text(jqXHR.responseText);
                    $('#notificationErrorModal').modal('show');
                }
            })
        }
        ;
    });


    //This button will be disable if there is no value input for [Input name for new Rank Title]
    {
        $('#button-add-new').hide();
        $('#input-new-title').change(function () {
            if ($('#input-new-title').val() == '') {
                $('#button-add-new').hide();
            } else {
                $('#button-add-new').show();
            }
        });
    }
    const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

    {
        $('#validate-alert').hide()
        $('#save-success').hide()
        //validate data before save
        $('#button-save').click(function () {
            //lấy tất cả các dòng trong table
            let rows = $('.table tbody tr').length;

            //nếu table rỗng
            if (rows == 0) {
                $('#notificationErrorModal .modal-body h5').text("Data table must not be empty!");
                $('#notificationErrorModal').modal('show');
            }
            else if(checkEmptyOption()){
                $('#notificationErrorModal .modal-body h5').text("Option must not be empty!");
                $('#notificationErrorModal').modal('show');

            }
            else if(checkDuplicatedRankScore()){
                $('#notificationErrorModal .modal-body h5').text("Duplicated Rank Score");
                $('#notificationErrorModal').modal('show');
            }
            //nếu table ko rỗng => có thể save
            else {
                console.log("remove: " + markedToRemove);
                markedToUpdate.forEach((value, key) => {
                    console.log(key + ':' + value);
                });
                var objectToUpdate = Object.fromEntries(markedToUpdate);
                var data = {
                    'rankTitlesToRemove': markedToRemove,
                    'rankTitlesToUpdate': objectToUpdate,
                }
                $.ajax({
                    url: '/api/ranking-decision-management-api/rank-title-config/' + $('#rankingDecisionId').val(),
                    type: 'PUT',
                    data: JSON.stringify(data),
                    contentType: 'application/json',
                    headers: {
                        [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
                    },
                    // contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                    processData: true, // jQuery sẽ tự động serialize dữ liệu
                    success: function (response, textStatus, jqXHR) {
                        if (jqXHR.status == 200) {
                            $('#notificationSuccessModal .modal-body h5').text(response);
                            $('#notificationSuccessModal').modal('show');
                            $('#notificationSuccessModal').on('hidden.bs.modal', function () {
                                location.reload();
                            });
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $('#notificationErrorModal .modal-body h5').text(jqXHR.responseText);
                        $('#notificationErrorModal').modal('show');
                    }
                });

            }

        });

    }


    $('#criteria-config').click(function (){
        if(edited){
            $('#notificationErrorModal .modal-body h5').text("You have to save changes first!");
            $('#notificationErrorModal').modal('show');
        }else{
            location.href='/ranking-decision-management/criteria-config/' + $('#rankingDecisionId').val()
        }
    });

    $('#task-n-price-config').click(function (){
        if(edited){
            $('#notificationErrorModal .modal-body h5').text("You have to save changes first!");
            $('#notificationErrorModal').modal('show');
        }else{
            location.href='/ranking-decision-management/task-n-price-config/' + $('#rankingDecisionId').val()
        }
    });


    var markedToUpdate = new Map();//element format: rankTitleId-optionId
    function addEventForSelectOption() {
        $('.option-select').change(function () {
            let key = $(this).attr('id'); // ranktitleid
            let value = $(this).find('option:selected').val(); //optionid
            let values = [];
            if(typeof markedToUpdate.get(key) !== 'undefined' && markedToUpdate.get(key) !== null){
                values = markedToUpdate.get(key);
            }
            if(value != '' && !values.includes(value)){
                values.push(value);
            }
            markedToUpdate.set(key, values);
        })
    }

    function calculateOrder() {
        $('#rank-title-table tbody tr').each(function (index) {
            // Set the first cell's text to the row number (index + 1)
            $(this).find('td:first').text(index + 1);
        });
    }

    function addEventForRemoveBtn() {
        $('.button-remove').each(function () {
            // Thêm hành động cho sự kiện click
            $(this).click(function () {
                if (!markedToRemove.includes(this.id)) {
                    markedToRemove.push(this.id);
                }
                markedToUpdate.delete(this.id);
                console.log(markedToRemove);
                //lấy phần tử cha của cha => thẻ butotn -> td -> tr
                var parentElement = this.parentElement.parentElement;
                parentElement.classList.add("removed-row");
                parentElement.remove();
            });
        });
    }

    //Calculate Rank Score
    function calculateRankScore() {
        $('.rank-title-row').each(function () {
            let sum = 0;
            $(this).find('option:selected').each(function () {
                if($(this).attr('data-optionScore') != ''){
                    sum += parseInt($(this).attr('data-optionScore') == '' ? 0 : $(this).attr('data-optionScore'))
                        * (parseFloat($(this).attr('data-weight')) / parseFloat($(this).attr('data-maxScore'))) ;
                }
            });
            $(this).find('.rank-score').text(sum.toFixed(2));
        });

        $('.option-select').change(function () {
            let sum =0;
            $(this).parent().parent().find('option:selected').each(function () {
                if($(this).attr('data-optionScore') != ''){
                    sum += parseInt($(this).attr('data-optionScore') == '' ? 0 : $(this).attr('data-optionScore'))
                        * (parseFloat($(this).attr('data-weight')) / parseFloat($(this).attr('data-maxScore'))) ;
                }
            });
            $(this).parent().parent().find('.rank-score').text(sum.toFixed(2));
        });
    }

    //kiem tra xem có option nào trống ko
    function checkEmptyOption(){
        let options = $('option:selected').map(function () {
            return $(this).attr('data-optionScore');
        }).get();

        return options.includes('');
    }
    function checkDuplicatedRankScore(){
        let rankScores = $('.rank-score').map(function () {
            return $(this).text();
        }).get();

        let uniqueRankScores = [...new Set(rankScores)];

        return rankScores.length != uniqueRankScores.length;
    }
}