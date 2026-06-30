let progressTimer = null;

$(document).ready(function() {

	$("#uploadBtn").click(uploadExcel);

	$("#generateBtn").click(generateReceipts);

	$("#cancelBtn").click(cancelProcess);

	$("#resetBtn").click(resetProcess);

});

/*----------------------------------------------------------
 Upload Excel
----------------------------------------------------------*/

function uploadExcel() {

	let file = $("#excelFile")[0].files[0];

	if (!file) {

		alert("Please select an Excel file.");

		return;
	}

	let formData = new FormData();

	formData.append("excelFile", file);

	$.ajax({

		url: "/donation/uploadExcel",

		type: "POST",

		data: formData,

		processData: false,

		contentType: false,

		success: function(response) {

			alert("Excel uploaded successfully.");
			loadProgress();

		},

		error: function() {

			alert("Unable to upload Excel.");

		}

	});

}

/*----------------------------------------------------------
 Generate
----------------------------------------------------------*/

function generateReceipts() {

	let outputType = $("#outputType").val();

	let sendWhatsapp = $("#sendWhatsapp").is(":checked");

	$("#generateBtn").prop("disabled", true);

	$("#uploadBtn").prop("disabled", true);

	$("#resetBtn").prop("disabled", true);

	$("#cancelBtn").prop("disabled", false);

	$.ajax({

	    url: "/donation/generate",

	    type: "POST",

	    data: {

	        outputType: outputType,

	        sendWhatsapp: sendWhatsapp

	    },

	    success: function () {

	        startProgress();

	    },

	    error: function () {

	        $("#generateBtn").prop("disabled", false);

	        $("#uploadBtn").prop("disabled", false);

	        $("#resetBtn").prop("disabled", false);

	        $("#cancelBtn").prop("disabled", true);

	        alert("Generation failed.");

	    }

	});

}

/*----------------------------------------------------------
 Progress
----------------------------------------------------------*/

function startProgress() {

	if (progressTimer != null) {

		clearInterval(progressTimer);

	}

	progressTimer = setInterval(loadProgress, 1000);

}

function loadProgress() {

	$.ajax({

		url: "/donation/progress",

		type: "GET",

		success: function(progress) {

		    updateProgress(progress);

		    // Cancelled
		    if (progress.currentStatus === "Cancelled") {

		        clearInterval(progressTimer);

		        progressTimer = null;

		        $("#generateBtn").prop("disabled", false);
		        $("#uploadBtn").prop("disabled", false);
		        $("#resetBtn").prop("disabled", false);
		        $("#cancelBtn").prop("disabled", true);

		        alert("Generation Cancelled");

		        return;
		    }

		    // Completed
		    if (progress.completed >= progress.total && progress.total > 0) {

		        clearInterval(progressTimer);

		        progressTimer = null;

		        $("#generateBtn").prop("disabled", false);
		        $("#uploadBtn").prop("disabled", false);
		        $("#resetBtn").prop("disabled", false);
		        $("#cancelBtn").prop("disabled", true);

		        alert("Generation Completed");
		    }

		}

	});

}

/*----------------------------------------------------------
 Update UI
----------------------------------------------------------*/

function updateProgress(progress) {

	$("#progressBar")
		.css("width", progress.percentage + "%")
		.text(progress.percentage + "%");

	$("#total").text(progress.total);

	$("#completed").text(progress.completed);

	$("#success").text(progress.success);

	$("#failed").text(progress.failed);

	$("#currentName").text(progress.currentName);

	$("#currentMobile").text(progress.currentMobile);

	$("#currentStatus").text(progress.currentStatus);

}

/*----------------------------------------------------------
 Cancel
----------------------------------------------------------*/

function cancelProcess() {

	$.ajax({

		url: "/donation/cancel",

		type: "POST",

		success: function() {

			clearInterval(progressTimer);

			$("#generateBtn").prop("disabled", false);

			$("#currentStatus").text("Cancelled");

			alert("Process cancelled.");

		},

		error: function() {

			alert("Unable to cancel process.");

		}

	});

}

/*----------------------------------------------------------
 Reset
----------------------------------------------------------*/

function resetProcess() {

	$.ajax({

		url: "/donation/reset",

		type: "POST",

		success: function() {

			clearInterval(progressTimer);

			$("#progressBar")
				.css("width", "0%")
				.text("0%");

			$("#total").text("0");

			$("#completed").text("0");

			$("#success").text("0");

			$("#failed").text("0");

			$("#currentName").text("-");

			$("#currentMobile").text("-");

			$("#currentStatus").text("Waiting...");

			$("#generateBtn").prop("disabled", false);

			$("#excelFile").val("");

			$("#sendWhatsapp").prop("checked", true);

			$("#outputType").val("JPG");

			alert("Reset completed.");

		}

	});

}