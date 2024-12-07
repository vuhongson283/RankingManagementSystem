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

        $('.money').change(function () {
            $('#button-cancel').prop("disabled", false);
            $('#button-save').prop("disabled", false);
            edited = true;
        });

        $('#button-add-new').click(function () {
            $('#button-cancel').prop("disabled", false);
            $('#button-save').prop("disabled", false);
            edited = true;
        });

    });
    $(document).ready(function (){
        addEventForRemoveBtn();
        addEventForInputInWorkingHour();
        addEventForInputOvertime()
    });


    //mảng lưu trữ những row đã xóa tạm thời(chưa cập nhật vào db cho đến khi save)
    var markedToRemove = [];

    //them task
    var markedToAdd = [];
    $("#button-add-new").click(function (){
        //nếu có criteria dc chọn thì add thêm criteria đó vào table
        if($('#select-new option:selected').val() != ""){
            let name = $('#select-new option:selected').attr('data-name');
            let taskId = $('#select-new option:selected').val();
            $('#select-new option:selected').remove();
            let htmlContent = '<tr data-taskid="'+taskId+'"> \n' +
                '                                <td rowspan="2">'+name+'</td>\n' +
                '                                <td>In working hour</td>';
            $('.rank-title-col').each(function () {
                htmlContent += ' <td> \n' +
                    '                 <input type="number" class="money" min="0" ' +
                    '                        value="0"' +
                    '                        data-taskid="'+taskId+'"\n' +
                    '                        data-ranktitleid="'+$(this).attr('data-ranktitleid')+'"\n' +
                    '                        data-type="in-working-hour"> \n ' +
                    '              </td> \n ';
            });
            htmlContent += ' <td rowspan="2"><div class="button-remove"\n' +
                '                                        id="'+taskId+'">\n' +
                '                                <i class="fa fa-trash" aria-hidden="true"></i>\n' +
                '                            </div></td>\n' +
                '                        </tr> ';
            htmlContent += '<tr data-taskid="'+taskId+'"> \n' +
                '                                <td>Overtime</td>';
            $('.rank-title-col').each(function () {
                htmlContent += ' <td> \n' +
                    '                 <input type="number" class="money" min="0" ' +
                    '                        value="0"' +
                    '                        data-taskid="'+taskId+'"\n' +
                    '                        data-ranktitleid="'+$(this).attr('data-ranktitleid')+'"\n' +
                    '                        data-type="overtime"> \n ' +
                    '              </td> \n '
            });
            htmlContent += ' </tr> ';
            console.log(htmlContent);
            $('tbody').append(htmlContent);
            markedToAdd.push(taskId);
            $('#default-select').prop('selected', true);
            $('#button-add-new').hide();
            console.log(markedToAdd);
        }

        //them click cho nhung criteria moi
        addEventForInputOvertime();
        addEventForInputInWorkingHour();
        addEventForRemoveBtn();

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
            //nếu table ko rỗng => có thể save
            else {
                console.log("remove: " + markedToRemove);
                console.log("add: " + markedToAdd);
                markedToUpdateInWorkingHour.forEach((value, key) => {
                    console.log(key + ':' + value);
                });
                markedToUpdateOvertime.forEach((value, key) => {
                    console.log(key + ':' + value);
                });
                var objectToUpdateInWorkingHour = Object.fromEntries(markedToUpdateInWorkingHour);
                var objectToUpdateOvertime = Object.fromEntries(markedToUpdateOvertime);
                var data = {
                    'tasksToRemove': markedToRemove,
                    'tasksToAdd':markedToAdd,
                    'inWorkingHourToUpdate': objectToUpdateInWorkingHour,
                    'overtimeToUpdate': objectToUpdateOvertime
                }
                $.ajax({
                    url: '/api/ranking-decision-management-api/task-n-price-config/' + $('#rankingDecisionId').val(),
                    type: 'PUT',
                    headers: {
                        [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
                    },
                    data: JSON.stringify(data),
                    contentType: 'application/json',
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

    $('#rank-title-config').click(function (){
        if(edited){
            $('#notificationErrorModal .modal-body h5').text("You have to save changes first!");
            $('#notificationErrorModal').modal('show');
        }else{
            location.href='/ranking-decision-management/rank-title-config/' + $('#rankingDecisionId').val()
        }
    });

    var markedToUpdateInWorkingHour = new Map();//key: rankTitleId-taskId          value: money
    function addEventForInputInWorkingHour() {
        $('.money[data-type="in-working-hour"]').change(function () {
            let key = $(this).attr('data-ranktitleid')+'-'+$(this).attr('data-taskid'); // ranktitleid-taskid
            console.log(key);
            let value = $(this).val(); //money
            markedToUpdateInWorkingHour.set(key, value);
        })
    }

    var markedToUpdateOvertime = new Map();//element format: rankTitleId-optionId
    function addEventForInputOvertime() {
        $('.money[data-type="overtime"]').change(function () {
            let key = $(this).attr('data-ranktitleid')+'-'+$(this).attr('data-taskid'); // ranktitleid-taskid
            console.log(key);
            let value = $(this).val(); //money
            markedToUpdateOvertime.set(key, value);
        })
    }

    function addEventForRemoveBtn() {
        $('.button-remove').each(function () {
            // Thêm hành động cho sự kiện click
            $(this).click(function () {
                if (!markedToRemove.includes(this.id)) {
                    markedToRemove.push(this.id);
                }
                console.log('remove: '+markedToRemove);
                $('tr[data-taskid="'+this.id+'"]').remove();
            });
        });
    }

    {
        $('#button-add-new').hide();
        $('#select-new').change(function () {
            let addNew = $("#button-add-new");
            if($('#select-new').val() == ''){
                addNew.hide();
            }else{
                addNew.show();
            }
        });
    }

}