{
    var edited = false;
    //nếu chưa có bất kì thay đổi nào -> disable button Cancel, Save
    $(document).ready(function (){
        $('#button-cancel').prop("disabled", true);
        $('#button-cancel')[0].classList.add('disabled');
        $('#button-save').prop("disabled", true);
        $('#button-save')[0].classList.add('disabled');

        $('.button-remove').click(function (){
            $('#button-cancel').prop("disabled", false);
            $('#button-save').prop("disabled", false);
            edited = true;
        });

        $('.weight').change(function () {
            $('#button-cancel').prop("disabled", false);
            $('#button-save').prop("disabled", false);
            edited = true;
        });

        $('#button-add-new').click(function (){
            $('#button-cancel').prop("disabled", false);
            $('#button-save').prop("disabled", false);
            edited = true;
        });
        addEventForRemoveBtn();
    });

    //mảng lưu trữ những row đã xóa tạm thời(chưa cập nhật vào db cho đến khi save)
    var markedToRemove = [];
    function addEventForRemoveBtn() {
        $('.button-remove').each(function () {
            // Thêm hành động cho sự kiện click
            $(this).click(function () {
                if (!markedToRemove.includes(this.id)) {
                    markedToRemove.push(this.id);
                }
                console.log(markedToRemove);
                //lấy phần tử cha của cha => thẻ butotn -> td -> tr
                var parentElement = this.parentElement.parentElement;
                parentElement.classList.add("removed-row");
                parentElement.remove();
            });
        });
    }

    var markedToAdd = [];
    $("#button-add-new").click(function (){
        //nếu có criteria dc chọn thì add thêm criteria đó vào table
        if($('#select-new option:selected').val() != ""){
            let name = $('#select-new option:selected').attr('data-name');
            let options = $('#select-new option:selected').attr('data-options');
            let maxscore = $('#select-new option:selected').attr('data-maxscore');
            let id = $('#select-new option:selected').val();
            $('#select-new option:selected').hide();
            let htmlContent = '<tr> \n' +
                '                                <td>'+name+'</td>\n' +
                '                                <td><input type="number"\n' +
                '                                           name="'+id+'"\n' +
                '                                           class="weight" min="0" max="100"\n' +
                '                                           value="0"\n' +
                '                                           >%</td>\n' +
                '                                <td>'+options+'</td>\n' +
                '                                <td>'+maxscore+'</td>\n' +
                '                                <td><div class="button-remove"\n' +
                '                                            id="'+id+'">\n' +
                '                                    <i class="fa fa-trash" aria-hidden="true"></i>\n' +
                '                                </div></td>\n' +
                '                            </tr>';
            console.log(htmlContent);
            $('tbody').append(htmlContent);
            markedToAdd.push(id);
            $('#default-select').prop('selected', true);
            console.log(markedToAdd);
        }

        //them click cho nhung criteria moi
        addEventForRemoveBtn();

    });

    //This button will be disable if there is no a value selected for [Select to Add a new Criteria]
    {
        $('#button-add-new').hide();
        $('#select-new').change(function () {
            let addNew = $("#button-add-new")[0];
            if($('#select-new').val() == ''){
                addNew.style.display = 'none';
            }else{
                addNew.style.display = 'inline';
            }
        });
    }

    {
        $('#validate-alert').hide()
        $('#save-success').hide()
        //validate data before save
        $('#button-save').click(function (){
            //lấy tất cả các dòng trong table
            let weights = $('.table tbody tr');
            let value=0;
            let isValid = true;
            weights.each(function (){
                //lấy những dòng chưa được đánh dấu là đã xóa
                if(!$(this)[0].classList.contains('removed-row')){
                    //nếu weight = 0 hiện cảnh báo
                    if($(this).find('input').val()==0){
                        $('#notificationErrorModal .modal-body h5').text("Weight must be greater than 0!");
                        $('#notificationErrorModal').modal('show');
                        isValid=false;
                    }
                    //tính tổng số weight
                    value += parseInt($(this).find('input').val());
                }
            });
            //nếu weight khác 100%, hiện cảnh báo
            if(value != 100 && isValid){
                $('#notificationErrorModal .modal-body h5').text("Total of Weight must be 100!");
                $('#notificationErrorModal').modal('show');
                isValid = false;
            }

            //nếu validate thành công -> save
            if(isValid){
                let weightsToChange = [];
                $('.weight').each(function (){
                    let name = $(this).attr("name");
                    let value = $(this).val();
                    weightsToChange.push(name+"-"+value);
                })
                console.log(markedToRemove);
                //rankingDecisionId: id của ranking decision hiện tại
                //criteriaToRemove: mảng id của những criteria cần xóa
                //weightsToChange: mảng những weight của những criteria cần thay đổi (format: criteriaId-weight)
                $.ajax({
                   url: '/api/ranking-decision-management-api/criteria-config/'+$('#rankingDecisionId').val(),
                    type: 'POST',
                    headers: {
                        [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
                    },
                    data:{
                       'criteriaToRemove' : markedToRemove,
                        'criteriaToAdd' : markedToAdd,
                        'weightsToChange': weightsToChange
                    },
                    contentType: 'application/x-www-form-urlencoded; charset=UTF-8', // Set contentType phù hợp
                    processData: true, // jQuery sẽ tự động serialize dữ liệu
                    success: function (response, textStatus, jqXHR){
                        if(jqXHR.status == 200){
                            $('#notificationSuccessModal .modal-body h5').text(response);
                            $('#notificationSuccessModal').modal('show');
                            $('#notificationSuccessModal').on('hidden.bs.modal', function () {
                                location.reload();
                            });
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown){
                        $('#notificationErrorModal .modal-body h5').text(jqXHR.responseText);
                        $('#notificationErrorModal').modal('show');
                    }
                });

            }
        });
    }

    $('#rank-title-config').click(function (){
        if(edited){
            $('#notificationErrorModal .modal-body h5').text("You have to save changes first!");
            $('#notificationErrorModal').modal('show');
        }else{
            location.href='/ranking-decision-management/rank-title-config/' + $('#rankingDecisionId').val()
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
}