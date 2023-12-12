# -*- coding: utf-8 -*-

# This sample demonstrates handling intents from an Alexa skill using the Alexa Skills Kit SDK for Python.
# Please visit https://alexa.design/cookbook for additional examples on implementing slots, dialog management,
# session persistence, api calls, and more.
# This sample is built using the handler classes approach in skill builder.
from datetime import datetime, time
import json
import logging
import ask_sdk_core.utils as ask_utils
import requests
import redis
import pytz

from ask_sdk_core.skill_builder import SkillBuilder
from ask_sdk_core.dispatch_components import AbstractRequestHandler
from ask_sdk_core.dispatch_components import AbstractExceptionHandler
from ask_sdk_core.handler_input import HandlerInput

from ask_sdk_model import Response

url_core = "https://ubiqui-care.fly.dev/api"
device = "device1"

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

r = redis.Redis(
  host='usw1-present-titmouse-34109.upstash.io',
  port=34109,
  password='631cd261bc524e0ba3a221ea534d020e'
)


class LaunchRequestHandler(AbstractRequestHandler):
    """Handler for Skill Launch."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool

        return ask_utils.is_request_type("LaunchRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "Welcome to Ubiqui Care. What assistance do you want?"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )


class UbiquiCareIntentHandler(AbstractRequestHandler):
    """Handler for Ubiqui Care Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("UbiquiCareIntent")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "Welcome to Ubiqui Care!"

        return (
            handler_input.response_builder
                .speak(speak_output)
                # .ask("add a reprompt if you want to keep the session open for the user to respond")
                .response
        )


class HelpRequestIntentHandler(AbstractRequestHandler):
    """Handler for HelpRequestIntent."""
    def can_handle(self, handler_input):
        return ask_utils.is_intent_name("HelpRequestIntent")(handler_input)

    def handle(self, handler_input):
    #   Define the responses
        help_responses = {
            'medical assistance': "I'll assist you with medical help.",
            'medical help': "I'll assist you with medical help. I have informed your contact person.",
            'general assistance': "I'm here to assist you with general help."
        }
        
        slots = handler_input.request_envelope.request.intent.slots
        if 'HelpType' in slots:
            help_type = slots['HelpType'].value.lower()
            
            speak_output = help_responses.get(help_type, "I'm sorry, I couldn't understand the type of help you need.")
            
            if help_type == 'medical assistance' or 'medical help':
                try:
                    api_url = url_core + "/event"
                    timestamp = datetime.utcnow().isoformat()
                    
                    body = {
                            "event_type": "emergency",
                            "event_level": "alert",
                            "timestamp": timestamp,
                            "device": device,
                            "datapoints": []
                            }
                    
                    response = requests.post(api_url, json=body)

                    if response.status_code == 200:
                        speak_output = speak_output + " I have informed your contact person."
                    else:
                        speak_output = speak_output + " I'm sorry, I couldn't reach anyone at the moment."
                except requests.exceptions.RequestException as e:
                    logger.error(f"Request Error: {e}")
                    speak_output = "Sorry, there was an error processing your request."
            
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("Can I assist you with something else?")
                .response
        )


class HealthDataIntentHandler(AbstractRequestHandler):
    def can_handle(self, handler_input):
        return ask_utils.is_intent_name("HealthDataIntent")(handler_input)

    def handle(self, handler_input):
        health_data_types = {
            'heart rate': "The selected data type is heart rate."
        }
        
        slots = handler_input.request_envelope.request.intent.slots
        if 'DataType' in slots:
            health_data_type = slots['DataType'].value.lower()
            
            speak_output = health_data_types.get(health_data_type, "I'm sorry, I couldn't understand the type of help you need.")
            
            try:
                api_url = url_core + "/heartrate"
                response = requests.get(api_url)
                if response.status_code == 200:
                    api_data = response.json()
                    
                    heart_rate_value = api_data.get('value', 'no data present')
                      
                    speak_output = f"Your current heart rate is {heart_rate_value}."
                
                else:
                    speak_output = "Sorry, I couldn't fetch the information at the moment."
            
            except requests.exceptions.RequestException as e:
                logger.error(f"Request Error: {e}")
                speak_output = "Sorry, there was an error processing your request."
                
        else:
            speak_output = "Please specify which health data you are interested in."
            
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("Can I assist you with something else?")
                .response
        )


class AverageHeartRateIntentHandler(AbstractRequestHandler):
    def can_handle(self, handler_input):
        return ask_utils.is_intent_name("AverageHeartRateIntent")(handler_input)
        
    def handle(self, handler_input):
        slots = handler_input.request_envelope.request.intent.slots
        if 'minutes' in slots and slots['minutes'].value != None:
            minutes = slots['minutes'].value
            
            try:
                api_url = url_core + "/summary/heartrate/average"
                params = {'minutes': minutes}
                response = requests.get(api_url, params=params)
                if response.status_code == 200:
                    api_data = response.json()
                    
                    heart_rate_value = api_data.get('value', 'no data present')
                      
                    speak_output = f"Your average heart rate was {heart_rate_value}."
                
                else:
                    speak_output = "Sorry, I couldn't fetch the information at the moment."
            
            except requests.exceptions.RequestException as e:
                logger.error(f"Request Error: {e}")
                speak_output = "Sorry, there was an error processing your request."
                
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("Can I assist you with something else?")
                .response
        )
    

class AddReminderIntentHandler(AbstractRequestHandler):
    def can_handle(self, handler_input):
        return ask_utils.is_intent_name("AddReminderIntent")(handler_input)
        
    def handle(self, handler_input):
        slots = handler_input.request_envelope.request.intent.slots
        if 'ReminderName' in slots and slots['ReminderName'].value != None:
            reminder_name = slots['ReminderName'].value.lower()
            r.set('current_reminder', reminder_name)
            
            speak_output = f"Setting a reminder for {reminder_name}. At which time do you want to be reminded?"
        else:
            speak_output = "Specify what the reminder is for."
        
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("Specify the time for the reminder.")
                .response
        )
    
    
class CheckOffReminderIntentHandler(AbstractRequestHandler):
    def can_handle(self, handler_input):
        return ask_utils.is_intent_name("CheckOffReminderIntent")(handler_input)
        
    def handle(self, handler_input):
        slots = handler_input.request_envelope.request.intent.slots
        if 'ReminderName' in slots and slots['ReminderName'].value != None:
            reminder_name = slots['ReminderName'].value.lower()
            
            try:
                api_url = url_core + "/notifications/check"
                
                response = requests.patch(api_url, params={'title': reminder_name})
                if response.status_code == 200:
                    speak_output = f"Checked off your reminder {reminder_name}."
                else:
                    speak_output = "Sorry, I couldn't check off the reminder."
            
            except requests.exceptions.RequestException as e:
                logger.error(f"Request Error: {e}")
                speak_output = "Sorry, there was an error processing your request."
        else:
            speak_output = "Specify what reminder you want to check off."
        
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("")
                .response
        )


class DeleteReminderIntentHandler(AbstractRequestHandler):
    def can_handle(self, handler_input):
        return ask_utils.is_intent_name("DeleteReminderIntent")(handler_input)
        
    def handle(self, handler_input):
        slots = handler_input.request_envelope.request.intent.slots
        if 'ReminderName' in slots and slots['ReminderName'].value != None:
            reminder_name = slots['ReminderName'].value.lower()
            
            try:
                api_url = url_core + "/notifications"
                
                response = requests.delete(api_url, params={'title': reminder_name})
                if response.status_code == 200:
                    speak_output = f"Deleted your reminder {reminder_name}."
                else:
                    speak_output = "Sorry, I couldn't delete the reminder."
            
            except requests.exceptions.RequestException as e:
                logger.error(f"Request Error: {e}")
                speak_output = "Sorry, there was an error processing your request."
        else:
            speak_output = "Specify what reminder you want to delete."
        
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("")
                .response
        )
        
    
class ReminderTimeIntentHandler(AbstractRequestHandler):
    def can_handle(self, handler_input):
        return ask_utils.is_intent_name("ReminderTimeIntent")(handler_input)
        
    def handle(self, handler_input):
        current_reminder_title = r.get('current_reminder').decode()

        slots = handler_input.request_envelope.request.intent.slots
        if 'ReminderDate' in slots and 'ReminderTime' in slots:
            reminder_date = slots['ReminderDate'].value
            reminder_time = slots['ReminderTime'].value
            
            try:
                api_url = url_core + "/notifications"
                
                timestamp = reminder_date + " " + reminder_time + ":00"
                local_time = datetime.strptime(timestamp, "%Y-%m-%d %H:%M:%S")
                us_pacific = pytz.timezone('America/Los_Angeles')
                local_time_pacific = us_pacific.localize(local_time)
                utc_time = local_time_pacific.astimezone(pytz.utc)
                formatted_time = utc_time.strftime("%Y-%m-%d %H:%M:%S")
                
                body = {
                    'title': current_reminder_title,
                    'timestamp': formatted_time
                }
                
                response = requests.post(api_url, json=body)
                if response.status_code == 200:
                    speak_output = f"Setting time for {current_reminder_title} to {reminder_date} at {reminder_time}."
                else:
                    speak_output = "Sorry, I couldn't add the reminder for this time."
            
            except requests.exceptions.RequestException as e:
                logger.error(f"Request Error: {e}")
                speak_output = "Sorry, there was an error processing your request."
            
        else:
            speak_output = "Specify the time for the reminder."
        
        
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("")
                .response
        )


class ReminderOverviewIntentHandler(AbstractRequestHandler):
    def can_handle(self, handler_input):
        return ask_utils.is_intent_name("ReminderOverviewIntent")(handler_input)
    
    def handle(self, handler_input):
        try:
            api_url = url_core + "/notifications/all"
            
            response = requests.get(api_url)
            if response.status_code == 200:
                reminders = response.json()
                
                speak_output = "Your reminders are: "
                for reminder in reminders:
                    title = reminder.get('title', '')
                    timestamp = reminder.get('timestamp', '')
                    
                    # Convert UTC timestamp to American West Coast time (Pacific Time)
                    utc_time = datetime.strptime(timestamp, "%Y-%m-%dT%H:%M:%S.%fZ")
                    utc_time = pytz.utc.localize(utc_time)
                    us_pacific = pytz.timezone('America/Los_Angeles')
                    local_time_pacific = utc_time.astimezone(us_pacific)
                    
                    # Formatting the timestamp to a more readable form
                    formatted_time_str = local_time_pacific.strftime("%B %d, %Y at %I:%M %p")
                    
                    speak_output += f"{title} at {formatted_time_str}. "
                
                if len(reminders) == 0:
                    speak_output = "You currently have no reminders."
                
            else:
                speak_output = "Sorry, I couldn't fetch the information at the moment."
        
        except requests.exceptions.RequestException as e:
            logger.error(f"Request Error: {e}")
            speak_output = "Sorry, there was an error processing your request."
        
        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("")
                .response
        )


class HelpIntentHandler(AbstractRequestHandler):
    """Handler for Help Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("AMAZON.HelpIntent")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "You can say hello to me! How can I help?"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )


class CancelOrStopIntentHandler(AbstractRequestHandler):
    """Single handler for Cancel and Stop Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return (ask_utils.is_intent_name("AMAZON.CancelIntent")(handler_input) or
                ask_utils.is_intent_name("AMAZON.StopIntent")(handler_input))

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "Goodbye!"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .response
        )

class FallbackIntentHandler(AbstractRequestHandler):
    """Single handler for Fallback Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("AMAZON.FallbackIntent")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        logger.info("In FallbackIntentHandler")
        speech = "Hmm, I'm not sure. You can check your health data, set reminders or call for help. What would you like to do?"
        reprompt = "I didn't catch that. What can I help you with?"

        return handler_input.response_builder.speak(speech).ask(reprompt).response

class SessionEndedRequestHandler(AbstractRequestHandler):
    """Handler for Session End."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_request_type("SessionEndedRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response

        # Any cleanup logic goes here.

        return handler_input.response_builder.response


class IntentReflectorHandler(AbstractRequestHandler):
    """The intent reflector is used for interaction model testing and debugging.
    It will simply repeat the intent the user said. You can create custom handlers
    for your intents by defining them above, then also adding them to the request
    handler chain below.
    """
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_request_type("IntentRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        intent_name = ask_utils.get_intent_name(handler_input)
        speak_output = "You just triggered " + intent_name + "."

        return (
            handler_input.response_builder
                .speak(speak_output)
                # .ask("add a reprompt if you want to keep the session open for the user to respond")
                .response
        )


class CatchAllExceptionHandler(AbstractExceptionHandler):
    """Generic error handling to capture any syntax or routing errors. If you receive an error
    stating the request handler chain is not found, you have not implemented a handler for
    the intent being invoked or included it in the skill builder below.
    """
    def can_handle(self, handler_input, exception):
        # type: (HandlerInput, Exception) -> bool
        return True

    def handle(self, handler_input, exception):
        # type: (HandlerInput, Exception) -> Response
        logger.error(exception, exc_info=True)

        speak_output = "Sorry, I had trouble doing what you asked. Please try again."

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )

# The SkillBuilder object acts as the entry point for your skill, routing all request and response
# payloads to the handlers above. Make sure any new handlers or interceptors you've
# defined are included below. The order matters - they're processed top to bottom.


sb = SkillBuilder()

sb.add_request_handler(LaunchRequestHandler())

sb.add_request_handler(UbiquiCareIntentHandler())
sb.add_request_handler(HelpRequestIntentHandler())
sb.add_request_handler(HealthDataIntentHandler())
sb.add_request_handler(AverageHeartRateIntentHandler())
sb.add_request_handler(AddReminderIntentHandler())
sb.add_request_handler(CheckOffReminderIntentHandler())
sb.add_request_handler(DeleteReminderIntentHandler())
sb.add_request_handler(ReminderTimeIntentHandler())
sb.add_request_handler(ReminderOverviewIntentHandler())

sb.add_request_handler(CancelOrStopIntentHandler())
sb.add_request_handler(FallbackIntentHandler())
sb.add_request_handler(SessionEndedRequestHandler())
sb.add_request_handler(IntentReflectorHandler()) # make sure IntentReflectorHandler is last so it doesn't override your custom intent handlers

sb.add_exception_handler(CatchAllExceptionHandler())

lambda_handler = sb.lambda_handler()
