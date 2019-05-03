#!/bin/bash
#set -x
input_file_1="model.hmr"
output_file_1="model2.hmr"
input_file_2="model2.hmr.hmr"
output_file_2="model.hmr.hmr"

###### INPUT BEGIN ######
declare -a input_array=(
"A1"
"A2"
"A3"
"agh.heart.callbacks.Location"
"agh.heart.callbacks.Gyroscope"
"agh.heart.callbacks.Screen"
"agh.heart.callbacks.Accelerometer"
"agh.heart.callbacks.HowAreYou_Color"
"agh.heart.callbacks.HowAreYou_Emotion"
"agh.heart.callbacks.HowAreYou_Photo"
"agh.heart.callbacks.HowAreYou_Settings"
"agh.heart.callbacks.HowAreYou_IsMoving"
"agh.heart.callbacks.HowAreYou_LatestAction"
"agh.heart.callbacks.Application_Facebook"
"agh.heart.callbacks.Application_GoogleMaps"
"agh.heart.callbacks.Application_YouTube"
"agh.heart.callbacks.Communication"
"agh.heart.actions.HowAreYou_StartQuestionColor"
"agh.heart.actions.HowAreYou_StartQuestionEmoji"
"agh.heart.actions.HowAreYou_NoAction"
)

declare -a output_array=(
",A,1,"
",A,2,"
",A,3,"
"C01"
"C02"
"C03"
"C04"
"C05"
"C06"
"C07"
"C08"
"C09"
"C10"
"C11"
"C12"
"C13"
"C14"
"A1"
"A2"
"A3"
)
###### INPUT END ######

process_file () {
    input_file="$1"
    output_file="$2"
    cp $input_file $output_file

    do_reverse_replacement=1
    for ((i=0;i<${#input_array[@]};++i)); do
        input_string="${input_array[i]}"
        if grep -q "$input_string" "$input_file"; then
            do_reverse_replacement=0
            break
        fi
    done

    for ((i=0;i<${#input_array[@]};++i)); do
        if (( $do_reverse_replacement == 1 )); then
            output_string="${input_array[i]}"
            input_string="${output_array[i]}"
        else
            input_string="${input_array[i]}"
            output_string="${output_array[i]}"
        fi
        sed "s/$input_string/$output_string/g" $output_file | tee $output_file > /dev/null
    done

    if (( $do_reverse_replacement == 1 )); then
        echo "Reverse replacement."

        #Replace ['-30.00' to 30.00] with [-30.00 to 30.00]
        sed -i.bak "s/'\(-.*\)'/\1/g" $output_file_2
    else
        echo "Direct replacement."
    fi
}

if [ -f "$input_file_1" ]; then
    process_file "$input_file_1" "$output_file_1"
fi
if [ -f "$input_file_2" ]; then
    process_file "$input_file_2" "$output_file_2"
fi
#set +x