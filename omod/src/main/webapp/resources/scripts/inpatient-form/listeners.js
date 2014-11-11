$(function() {


  $(".ebola-form .section").on("click", ".radio-button", function(event) {
    var $input = $("#" + $(this).attr("for"));

    var type = $input.attr("type");

    if (type !== "radio") {
      return;
    }

     var scope = angular.element($("#simpleController")).scope();
    var previousValue = $input.attr("check");

    if (previousValue === "true") {
      $input.prop("checked", false);
      $input.attr("check", false);

     
      scope.$apply(function() {
        scope.viewModel[$input.attr("name")] = "";
      });

      event.preventDefault();
    } else {
      $input.attr("check", true);
      scope.$apply(function() {
        scope.viewModel[$input.attr("name")] = $input.val();
      });
    }

    console.log(scope.viewModel);

    event.stopPropagation();

  });

});
